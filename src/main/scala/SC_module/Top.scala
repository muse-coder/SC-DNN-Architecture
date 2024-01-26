package SC_module
import chisel3._
import chisel3.util._
import chisel3.stage._
class Top extends Module {
  val io= IO(new Bundle {
    val flint_weight = Input(UInt(4.W))
    val flint_act = Input(UInt(4.W))
    val res = Output(UInt(8.W))
  })
  val stochaDecoder = Module(new DecoderInfo(true))
  val uanryDecoder = Module(new DecoderInfo(false))
  stochaDecoder.io.flint := io.flint_act
  uanryDecoder.io.flint := io.flint_weight
  val parallelCompute = Module(new ParallelCompute)
  parallelCompute.io.stochaIO <> stochaDecoder.io.streamIO
  parallelCompute.io.unaryIO <> uanryDecoder.io.streamIO
  io.res := parallelCompute.io.finalRes

//  printf(p"weight: ${io.flint_weight} , act:${io.flint_act}\n")
}

object generateTop extends App{
  new (chisel3.stage.ChiselStage).emitVerilog(
      new Top(),
      Array(
        "--target-dir","output/Top"
      )
  )
}

