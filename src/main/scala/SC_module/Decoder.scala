 package SC_module
 import chisel3._
 import chisel3.util._
 import chisel3._
 import chisel3.experimental.ChiselEnum
 object dataNum extends ChiselEnum{
   val five , six , seven , eight = Value
 }
 object UnaryStr extends ChiselEnum{
   val  five   = "b11111000".U
   val  six    = "b11111100".U
   val  seven  =  "b11111110".U
   val  eight  =  "b11111111".U
 }
 object StochStr extends ChiselEnum{
   val  five   =  "b11011001".U
   val  six    =  "b11011011".U
   val  seven  =  "b11111011".U
   val  eight  =  "b11111111".U
 }
 class StreamIO extends Bundle {
   val stream = UInt(8.W)
   val shiftNum = UInt(3.W)
 }
 class DecodeIO  extends Bundle{
   val flint = Input(UInt(4.W))
   val streamIO = Output(new StreamIO)
 }
class DataDecoder () extends Module{
  val flint = IO(Input(UInt(4.W)))
  val data = IO(Output(UInt(dataNum.getWidth.W)))
  assert(flint.getWidth==4,"width error")
  val A = flint(3)
  val B = flint(2)
  val C = flint(1)
  val D = flint(0)
  val check8 = !(C|D) | ( (!B) & (C^D))
  val check7 = B & C & D
  val check6 = C &(B ^ D)
  val check5 = B &(!C) & D
  data := PriorityMux(Array(
    check5  ->  dataNum.five.asUInt,
    check6  ->  dataNum.six.asUInt,
    check7  ->  dataNum.seven.asUInt,
    check8  ->  dataNum.eight.asUInt
  ))
//  printf(p"Print during simulation: flint:${flint} , check8:${check8} , check7:${check7}, check6:${check6} , check5:${check5}\n")
//  printf(p"Print during simulation: flint:${flint} , check8:${check8} , check7:${check7}, check6:${check6} , check5:${check5}\n")
}


class shiftTime extends Module{
  val flint  = IO(Input(UInt(4.W)))
  val shiftNum = IO(Output(UInt(2.W)))
  val A = flint(3)
  val B = flint(2)
  val C = flint(1)
  val D = flint(0)
  val check0 = (!A) & B & (C|D) | (A&B&(!C)&(!D))
  val check2 = !B  & ( (!A & !D) | (A & D))
  val check3 = !B & !C & (A ^ D)
  val check1 = !(check0|check2|check3)
  shiftNum := PriorityMux(Array(
    check0 -> 0.U,
    check1 -> 1.U,
    check2 -> 2.U,
    check3 -> 3.U,
  ))
//  printf(p"flint:${flint} check0: $check0 check1: $check1 check2: $check2 check3: $check3 \n")
//  printf(p"error: ${!(check0| check1| check2| check3)} \n")

}

class shiftDirection extends  Module{
  val flint  = IO(Input(UInt(4.W)))
  val direction = IO(Output(UInt(1.W)))
  val A = flint(3)
  val B = flint(2)
  val C = flint(1)
  val D = flint(0)
  val left = ((!A) & (!B) )| (!A & B & !C & !D)
  direction := Mux(left,1.U,0.U) //0->left 1->right
}

class DecoderInfo (StochOrUnary:Boolean) extends  Module {
  val io = IO(new DecodeIO)
  val dataDecoder = Module(new DataDecoder)
  dataDecoder.flint := io.flint
  val data = dataDecoder.data
  io.streamIO.stream:=  PriorityMux(Array(
    (data===dataNum.five.asUInt)  ->  (if(StochOrUnary==true) StochStr.five else   UnaryStr.five) ,
    (data===dataNum.six.asUInt)   ->  (if(StochOrUnary==true) StochStr.six else   UnaryStr.six) ,
    (data===dataNum.seven.asUInt) ->  (if(StochOrUnary==true) StochStr.seven else   UnaryStr.seven) ,
    (data===dataNum.eight.asUInt) ->  (if(StochOrUnary==true) StochStr.eight else   UnaryStr.eight)
    )
  )
//  printf(p"StochOrUnary = ${StochOrUnary} ")
  val shiftTime = Module(new shiftTime)
  val shiftDire =Module(new shiftDirection)
  shiftTime.flint := io.flint
  shiftDire.flint := io.flint
  val shift1 = ((~Cat(0.U, shiftTime.shiftNum)).asUInt + 1.U)

  val shift2 = (Cat(0.U,shiftTime.shiftNum))
  io.streamIO.shiftNum := Mux(shiftDire.direction===1.U,shift1,shift2)

//  printf(p"shift1 : ${shift1} shift2 :${shift2} shiftTime :${shiftTime.shiftNum} shiftDire:${shiftDire.direction}\n") //0->left 1->right

}
