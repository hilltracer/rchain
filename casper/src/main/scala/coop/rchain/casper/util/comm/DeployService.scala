package coop.rchain.casper.util.comm

import java.util.concurrent.TimeUnit

import cats.implicits._

import com.google.protobuf.empty.Empty
import io.grpc.{ManagedChannel, ManagedChannelBuilder}
import coop.rchain.casper.protocol.{BlockMessage, BlockQuery, DeployServiceGrpc, DeployString}

import monix.eval.Task

trait DeployService[F[_]] {
  def deploy(d: DeployString): F[(Boolean, String)]
  //Attempt to create a new block. Note: block is
  //returned UNSIGNED so it is up to the client to
  //add the appropriate values of the sender, sig and sigAlgorithm fields.
  def createBlock(): F[Option[BlockMessage]]
  def showBlock(q: BlockQuery): F[String]
  def showBlocks(): F[String]
  def addBlock(b: BlockMessage): F[(Boolean, String)] //add a block to Casper internal state
}

object DeployService {
  def apply[F[_]](implicit ev: DeployService[F]): DeployService[F] = ev
}

class GrpcDeployService(host: String, port: Int) extends DeployService[Task] {

  private val channel: ManagedChannel =
    ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build
  private val blockingStub = DeployServiceGrpc.blockingStub(channel)

  def deploy(d: DeployString): Task[(Boolean, String)] = Task.delay {
    val response = blockingStub.doDeploy(d)
    (response.success, response.message)
  }

  def createBlock(): Task[Option[BlockMessage]] =
    Task.delay {
      blockingStub.createBlock(Empty()).block
    }

  def showBlock(q: BlockQuery): Task[String] = Task.delay {
    val response = blockingStub.showBlock(q)
    response.toProtoString
  }

  def showBlocks(): Task[String] = Task.delay {
    val response = blockingStub.showBlocks(Empty())
    response.toProtoString
  }

  def addBlock(b: BlockMessage): Task[(Boolean, String)] = Task.delay {
    val response = blockingStub.addBlock(b)
    (response.success, response.message)
  }

  def close(): Unit = channel.shutdown().awaitTermination(3, TimeUnit.SECONDS)
}
