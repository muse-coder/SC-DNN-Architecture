package SC_module
import chisel3._
import  chisel3.util._
class ParallelCompute extends Module{
  val io = IO(new Bundle {
    val unaryIO = Input(new StreamIO)
    val stochaIO = Input(new StreamIO)
    val finalRes = Output(UInt(16.W))
  })
  val andResult = io.unaryIO.stream & io.stochaIO.stream
  val APC = Module(new APC_8)
  APC.stream := andResult
  val count = APC.res
  val totalLeftShift =  (3.S +& (io.unaryIO.shiftNum.asSInt +& io.stochaIO.shiftNum.asSInt)).asSInt
  val totalLeftShift_abs = Mux(totalLeftShift>=0.S,totalLeftShift.asUInt, (~totalLeftShift).asUInt+1.U)
  val finalRes_1 = count.asUInt << totalLeftShift_abs
  val finalRes_2 = count.asUInt >> totalLeftShift_abs
//  printf(p"unaryIO.shiftNum:${io.unaryIO.shiftNum},stochaIO.shiftNum:${io.stochaIO.shiftNum}\n")
//  printf(cf"unarystream:${io.unaryIO.stream}%b , stochaStream :${io.stochaIO.stream }%b ,\n andRes:${andResult}%b APC:${count}  \n")
//  printf(p"totalLeftShift:${totalLeftShift} ,${totalLeftShift_abs.asUInt} , finalRes_1 :${finalRes_1 } ,finalRes_2:${finalRes_2 } \n")
  io.finalRes := Mux(totalLeftShift.asSInt>=0.S,finalRes_1 , finalRes_2)
//  printf(p"io.finalRes :${io.finalRes } \n")
}
