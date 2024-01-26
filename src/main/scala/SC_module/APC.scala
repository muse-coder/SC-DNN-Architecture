package SC_module
import chisel3._
import chisel3.util._
class FA extends Module{
  val io = IO(new Bundle {
    val a = Input(UInt(1.W))
    val b = Input(UInt(1.W))
    val cin = Input(UInt(1.W))
    val sum = Output(UInt(1.W))
    val cout = Output(UInt(1.W))
  })
  io.sum := io.a ^ io.b ^ io.cin
  io.cout := (io.a & io.b) | (io.b & io.cin) | (io.cin & io.a)
}
class APC_8 extends Module {
  val stream = IO(Input(UInt(8.W)))
  val res = IO(Output(UInt(4.W)))
  val FAs = Array.fill(2,2){  Module (new FA)}
    FAs(0)(0).io.a := stream(0)
    FAs(0)(0).io.b := stream(1)
    FAs(0)(0).io.cin := stream(2)
    FAs(0)(1).io.a := stream(3)
    FAs(0)(1).io.b := stream(4)
    FAs(0)(1).io.cin := stream(5)

    FAs(1)(0).io.a := FAs(0)(0).io.cout
    FAs(1)(0).io.b := FAs(0)(1).io.cout
    FAs(1)(0).io.cin := FAs(1)(1).io.cout

    FAs(1)(1).io.a := FAs(0)(0).io.sum
    FAs(1)(1).io.b := FAs(0)(1).io.sum
    FAs(1)(1).io.cin := stream(6)
  res :=Cat(FAs(1)(0).io.cout, FAs(1)(0).io.sum , FAs(1)(1).io.sum) +& stream(7)
}
