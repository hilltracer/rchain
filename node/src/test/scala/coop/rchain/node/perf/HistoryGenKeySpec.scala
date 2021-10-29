package coop.rchain.node.perf

import cats.Parallel
import cats.effect.{Concurrent, ContextShift, Sync}
import cats.syntax.all._
import coop.rchain.crypto.codec.Base16
import coop.rchain.metrics.{Metrics, NoopSpan, Span}
import coop.rchain.rspace.hashing.Blake2b256Hash
import coop.rchain.rspace.history.HistoryInstances.{CachingHistoryStore, MergingHistory}
import coop.rchain.rspace.history._
import coop.rchain.rspace.history.instances._
import coop.rchain.shared.Log
import coop.rchain.store.{InMemoryKeyValueStore, LmdbStoreManager}
import org.scalatest.{FlatSpec, Matchers}
import scodec.bits.ByteVector

import java.io.File
import java.math.BigInteger
import java.nio.file.{Path, Paths}
import scala.collection.concurrent.TrieMap
import scala.concurrent.ExecutionContext
import scala.language.higherKinds

import coop.rchain.rspace.history.instances.RadixHistory7

class HistoryGenKeySpec extends FlatSpec with Matchers {

  //Experiment options
//  val typeHistory: String = "RadixHistory"
//  val typeHistory: String = "RadixHistory3"
//  val typ eHistory: String = "RadixHistory4InMemo"
//  val typeHistory: String = "RadixHistory4InLMDB"
//  val typeHistory: String = "RadixHistory5InMemo"
//  val typeHistory: String = "RadixHistory5InLMDB"
//  val typeHistory: String = "RadixHistory6InMemo"
//  val typeHistory: String = "RadixHistory6InLMDB"
//  val typeHistory: String = "RadixHistory7InMemo"
  val typeHistory: String = "RadixHistory7InLMDB"
//  val typeHistory: String = "MergingHistoryInMemo"
//  val typeHistory: String = "MergingHistoryInLMDB"

//  val readTest: Boolean = false
  val readTest: Boolean = true

//  val deleteCorrectnesTest: Boolean = false
  val deleteCorrectnesTest: Boolean = true

  val smallTest: Boolean = false
//  val smallTest: Boolean = true

  class TestCase[F[_]: Concurrent: ContextShift: Parallel: Log: Metrics: Span](
      implicit ectx: ExecutionContext
  ) extends HistoryHelpers[F] {

    override def init: Deps =
      (ectx, Concurrent[F], ContextShift[F], Parallel[F], Log[F], Metrics[F], Span[F])

    case class S(name: String, genCount: Int)

    def setup1: F[List[Unit]] =
      // Generate pairs
      (if (smallTest)
         List(
//           S("gen-0.01", 10)
           S("gen-0.01", 10),
           S("gen-0.3", 300),
           S("gen-1", 1000),
           S("gen-10", 10000),
           S("gen-20", 20000),
           S("gen-30", 30000)
         )
       else
         List(
//           S("gen-350k", 500000)
           S("gen-50k", 50000),
           S("gen-100k", 100000),
           S("gen-150k", 150000),
           S("gen-200k", 200000),
           S("gen-250k", 250000),
           S("gen-300k", 300000),
           S("gen-350k", 350000),
           S("gen-400k", 400000),
           S("gen-450k", 450000),
           S("gen-500k", 500000)
         )).traverse {
        case S(_, genCount) =>
          def k(s: String) = {
            val prefix = (0 to 31 - (s.length / 2)).map(_ => "11").mkString
            Base16.unsafeDecode(prefix + s)
          }
//          def k_null(s: String) = {
//            val prefix = (0 to 31 - (s.length / 2)).map(_ => "00").mkString
//            Base16.unsafeDecode(prefix + s)
//          }

          val v = Blake2b256Hash.fromByteArray(k("99"))
//          val v_null = Blake2b256Hash.fromByteArray(k_null("00"))

          val step = 3
          def genTasks = {
            val k      = step // step
            val genMax = genCount * k
            (0 until genMax by k).iterator.map { i =>
              val bytes = BigInteger.valueOf(i.toLong).toByteArray
              Blake2b256Hash.create(bytes)
            }
          }

          def genInitTasks = {
            val k      = step // step
            val genMax = genCount * k
            (1 until genMax by k).iterator.map { i =>
              val bytes = BigInteger.valueOf(i.toLong).toByteArray
              Blake2b256Hash.create(bytes)
            }
          }

          def deleteFile(path: String) =
            new File(path).delete()

          def genInsertTasks(tasks: List[Blake2b256Hash]) =
            tasks.map { t =>
              InsertAction(t.bytes.toArray.toList, t)
            }

          def genReadTasks(tasks: List[Blake2b256Hash]) =
            tasks.map { t =>
              t.bytes
            }

          def readAndVerify(tasks: List[ByteVector], h: History[F]) =
            tasks.traverse { t =>
              h.read(t).map { leafValue =>
                {
                  assert(leafValue.contains(t), "Test read not passed")
                  leafValue
                }
              }
            }

          def genDeleteTasks(tasks: List[Blake2b256Hash]) =
            tasks.map { t =>
              DeleteAction(t.bytes.toArray.toList)
            }

          def saveGroups(tasks: List[List[Blake2b256Hash]], h: History[F]): F[History[F]] =
            tasks match {
              case List() =>
                h.pure[F]
              case next +: tail =>
                for {
                  insertTasks <- Sync[F].delay(genInsertTasks(next))
                  hh          <- h.process(insertTasks)
                  r           <- saveGroups(tail, hh)
                } yield r
            }

          def readGroups(tasks: List[List[Blake2b256Hash]], h: History[F]): F[History[F]] =
            tasks match {
              case List() =>
                h.pure[F]
              case next +: tail =>
                for {
                  readTasks <- Sync[F].delay(genReadTasks(next))
                  _         <- readAndVerify(readTasks, h)
                  r         <- readGroups(tail, h)
                } yield r
            }

          def deleteGroups(tasks: List[List[Blake2b256Hash]], h: History[F]): F[History[F]] =
            tasks match {
              case List() =>
                h.pure[F]
              case next +: tail =>
                for {
                  deleteTasks <- Sync[F].delay(genDeleteTasks(next))
                  hh          <- h.process(deleteTasks)
                  r           <- deleteGroups(tail, hh)
                } yield r
            }

          // Save all with history process

          //          val dataDir = Paths.get("~/temp")
          val dataDirLMDBmain       = Paths.get("/git/temp")
          val dataDirLMDBwarmUp     = Paths.get("/git/temp2")
          val storeNameLMDB: String = "db"

          def createHistory(dataDirLMDB: Path): F[History[F]] =
            for {

              lmdbHistoryManager <- LmdbStoreManager(
                                     dataDirLMDB,
                                     8L * 1024 * 1024 * 1024
                                   )
              lmdbHistoryStore <- lmdbHistoryManager.store(storeNameLMDB)

              history = typeHistory match {
                case "RadixHistory" =>
                  val store = TrieMap[ByteVector, ByteVector]()
                  RadixHistory(v, store)
                case "RadixHistory3" =>
                  val store = TrieMap[ByteVector, ByteVector]()
                  RadixHistory3(v, store)
                case "RadixHistory4InMemo" =>
                  val store = new RadixStore(InMemoryKeyValueStore[F])
                  RadixHistory4(v, store)
                case "RadixHistory4InLMDB" =>
                  val store = new RadixStore(lmdbHistoryStore)
                  RadixHistory4(v, store)
                case "RadixHistory5InMemo" =>
                  val store = new RadixStore(InMemoryKeyValueStore[F])
                  RadixHistory5(v, store)
                case "RadixHistory5InLMDB" =>
                  deleteFile(dataDirLMDB + "/data.mdb")
                  deleteFile(dataDirLMDB + "/lock.mdb")
                  val store = new RadixStore(lmdbHistoryStore)
                  RadixHistory5(v, store)
                case "RadixHistory6InMemo" =>
                  val store = new RadixStore(InMemoryKeyValueStore[F])
                  RadixHistory6(v, store)
                case "RadixHistory6InLMDB" =>
                  deleteFile(dataDirLMDB + "/data.mdb")
                  deleteFile(dataDirLMDB + "/lock.mdb")
                  val store = new RadixStore(lmdbHistoryStore)
                  RadixHistory6(v, store)
                case "RadixHistory7InMemo" =>
                  val store = new RadixStore(InMemoryKeyValueStore[F])
                  RadixHistory7(v, store)
                case "RadixHistory7InLMDB" =>
                  deleteFile(dataDirLMDB + "/data.mdb")
                  deleteFile(dataDirLMDB + "/lock.mdb")
                  val store = new RadixStore(lmdbHistoryStore)
                  RadixHistory7(v, store)
                case "MergingHistoryInMemo" =>
                  val store = HistoryStoreInstances.historyStore(InMemoryKeyValueStore[F])
                  MergingHistory(v, CachingHistoryStore(store))
                case "MergingHistoryInLMDB" =>
                  deleteFile(dataDirLMDB + "/data.mdb")
                  deleteFile(dataDirLMDB + "/lock.mdb")
                  val store = HistoryStoreInstances.historyStore(lmdbHistoryStore)
                  MergingHistory(v, CachingHistoryStore(store))
              }
            } yield history

//          def sToBArr(str: String): List[Byte] = ByteVector.fromHex(str).get.toArray.toList

          val saveHistory = for {
            initTasks <- Sync[F].delay(genInitTasks.toList)
            tasks     <- Sync[F].delay(genTasks.toList)

            _           <- shortLog(s"${tasks.size} ")
            sizeInGroup = 100000000

            historySaveW <- createHistory(dataDirLMDBwarmUp) //for warm up
            historySave  <- createHistory(dataDirLMDBmain)

            initilalHistoryW <- saveGroups(initTasks.grouped(sizeInGroup).toList, historySaveW)
            initilalHistory  <- saveGroups(initTasks.grouped(sizeInGroup).toList, historySave)

            saveAllW = saveGroups(tasks.grouped(sizeInGroup).toList, initilalHistoryW)
            saveAll  = saveGroups(tasks.grouped(sizeInGroup).toList, initilalHistory)

            saveHistoryW <- (saveAllW)
            saveHistory  <- msTime(saveAll)

            _ <- shortLog(" ")

            readAllW = readGroups(tasks.grouped(sizeInGroup).toList, saveHistoryW)
            readAll  = readGroups(tasks.grouped(sizeInGroup).toList, saveHistory)
            _ <- {
              for {
                _ <- (readAllW)
                _ <- msTime(readAll)
                _ <- shortLog(" ")
              } yield ()
            }.whenA(readTest)

            deleteAllW = deleteGroups(tasks.grouped(sizeInGroup).toList, saveHistoryW)
            deleteAll  = deleteGroups(tasks.grouped(sizeInGroup).toList, saveHistory)

            deleteHistoryW <- (deleteAllW)
            deleteHistory  <- msTime(deleteAll)

            _ <- shortLog("\n")

            _ <- Sync[F]
                  .delay(
                    assert(initilalHistory.root == deleteHistory.root, "Test delete not passed")
                  )
                  .whenA(deleteCorrectnesTest)
            // Results
            result = (tasks, deleteHistory.root)

          } yield result

          for {
            _ <- saveHistory
          } yield ()
      }

    val setup: F[Unit] =
      for {
        _ <- log(typeHistory)
        _ <- log(
              if (readTest) s"[Num record] [insert(ms)] [read(ms)] [delete(ms)]"
              else s"[Num record] [insert(ms)] [delete(ms)]"
            )
        _ <- setup1
      } yield ()
  }

  it should "execute with monix" in {
    import monix.eval.Task
    import monix.execution.Scheduler.Implicits.global

    implicit val log: Log.NOPLog[Task]         = new Log.NOPLog[Task]()
    implicit val met: Metrics.MetricsNOP[Task] = new Metrics.MetricsNOP[Task]()
    implicit val spn: NoopSpan[Task]           = new NoopSpan[Task]()

    val t = new TestCase[Task]
    t.setup.runSyncUnsafe()
  }
}
