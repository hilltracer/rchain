package coop.rchain.models.rholangN

import org.openjdk.jmh.annotations._
import scodec.bits.ByteVector

import java.util.concurrent.TimeUnit
import scala.annotation.tailrec

@Fork(value = 1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OperationsPerInvocation(value = 100)
@State(Scope.Benchmark)
class ParBench {

  @tailrec
  final def createNestedPar(n: Int, par: Par = GInt(0)): Par =
    if (n == 0) par
    else createNestedPar(n - 1, EList(par))

  final def createParProc(n: Int): Par = {
    val elSize     = 33
    def el(i: Int) = EList(Seq.fill(elSize)(GInt(i.toLong)))
    val seq        = Seq.tabulate(n)(el)
    ParProc(seq)
  }

  final def appendTest(n: Int): Par = {
    val elSize     = 33
    def el(i: Int) = EList(Seq.fill(elSize)(GInt(i.toLong)))

    val seq = Seq.tabulate(n)(el)
    seq.foldLeft(ParProc(Seq())) { (acc, p) =>
      acc.add(p)
    }
  }
  val nestedSize: Int            = 500
  var nestedPar: Par             = _
  var nestedAnotherPar: Par      = _
  var nestedParSData: ByteVector = _

  val parProcSize: Int         = 500
  var parProc: Par             = _
  var parProcAnother: Par      = _
  var parProcSData: ByteVector = _

  @Setup(Level.Iteration)
  def setup(): Unit = {
    nestedPar = createNestedPar(nestedSize)
    nestedAnotherPar = createNestedPar(nestedSize)
    nestedParSData = nestedPar.toBytes

    parProc = createParProc(parProcSize)
    parProcAnother = createParProc(parProcSize)
    parProcSData = parProc.toBytes
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def nestedCreation(): Unit = {
    val _ = createNestedPar(nestedSize)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def nestedSerialization(): Unit = {
    val _ = nestedPar.toBytes
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def nestedDeserialization(): Unit = {
    val _ = Par.fromBytes(nestedParSData)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def nestedSerializedSize(): Unit = {
    val _ = nestedPar.serializedSize
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def nestedHash(): Unit = {
    val _ = nestedPar.rhoHash
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def nestedEqual(): Unit = {
    val _ = nestedPar.equals(nestedAnotherPar)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def nestedAdd(): Unit =
    ParProc(Seq(nestedPar, GInt(0)))

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def parProcCreation(): Unit = {
    val _ = createParProc(parProcSize)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def parProcSerialization(): Unit = {
    val _ = parProc.toBytes
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def parProcDeserialization(): Unit = {
    val _ = Par.fromBytes(parProcSData)
  }
  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def parProcSerializedSize(): Unit = {
    val _ = parProc.serializedSize
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def parProcHash(): Unit = {
    val _ = parProc.rhoHash
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def parProcEqual(): Unit = {
    val _ = parProc.equals(parProcAnother)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def parProcAdd(): Unit = {
    val _ = parProc match {
      case proc: ParProc => proc.add(GInt(0))
      case _             => assert(false)
    }
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def manyAppends(): Unit = {
    val _ = appendTest(parProcSize)
  }
}
