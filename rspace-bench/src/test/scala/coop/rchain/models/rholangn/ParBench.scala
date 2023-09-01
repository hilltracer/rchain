package coop.rchain.models.rholangn

import coop.rchain.models.rholangn.ParN.toBytes
import coop.rchain.models.rholangn.parmanager.{Manager, Serialization}
import org.openjdk.jmh.annotations._

import java.util.concurrent.TimeUnit
import scala.annotation.tailrec

@Fork(value = 1)
@Warmup(iterations = 5)
@Measurement(iterations = 3)
@OperationsPerInvocation(value = 10)
@State(Scope.Benchmark)
class ParBench {

  @tailrec
  final def createNestedPar(n: Int, par: ParN = GIntN(0)): ParN =
    if (n == 0) par
    else createNestedPar(n - 1, EListN(par))

  final def createParProc(n: Int): ParN = {
    val elSize     = 33
    def el(i: Int) = EListN(Seq.fill(elSize)(GIntN(i.toLong)))
    val seq        = Seq.tabulate(n)(el)
    ParN.makeParProc(seq)
  }

  final def appendTest(n: Int): ParN = {
    val elSize     = 33
    def el(i: Int) = EListN(Seq.fill(elSize)(GIntN(i.toLong)))

    val seq = Seq.tabulate(n)(el)
    seq.foldLeft(NilN: ParN) { (acc, p) =>
      ParN.combine(acc, p)
    }
  }
  val nestedSize: Int  = 100
  val parProcSize: Int = 100

  var nestedPar: ParN             = NilN
  var nestedAnotherPar: ParN      = NilN
  var nestedParSData: Array[Byte] = Array()

  var parProc: ParN             = NilN
  var parProcAnother: ParN      = NilN
  var parProcSData: Array[Byte] = Array()

  @Setup(Level.Invocation)
  def setup(): Unit = {
    nestedPar = createNestedPar(nestedSize)
    nestedAnotherPar = createNestedPar(nestedSize)
    nestedParSData = toBytes(createNestedPar(nestedSize))

    parProc = createParProc(parProcSize)
    parProcAnother = createParProc(parProcSize)
    parProcSData = toBytes(createParProc(parProcSize))
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
    val _ = toBytes(nestedPar)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def nestedDeserialization(): Unit = {
    val _ = ParN.fromBytes(nestedParSData)
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
    ParProcN(Seq(nestedPar, GIntN(0)))

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
    val _ = toBytes(parProc)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def parProcDeserialization(): Unit = {
    val _ = ParN.fromBytes(parProcSData)
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
      case proc: ParProcN => ParN.combine(proc, GIntN(0))
      case _              => assert(false)
    }
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def manyAppends(): Unit = {
    val _ = appendTest(parProcSize)
  }
}
