package gcd


import SC_module.{APC_8, DecoderInfo, Top}
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import java.io._
import scala.math.pow


class Test_SC_Module extends AnyFlatSpec with ChiselScalatestTester {

  "Test APC" should "pass" in {
    def countOnes(binaryString: String): Int = {
      binaryString.count(_ == '1')
    }
    test(new APC_8 ) { dut =>
      for (i<-0 until 100){
        val randomData = scala.util.Random.nextInt(((pow(2,8)-1).toInt))
        val randomDataBinary = randomData.toBinaryString
        val correctCount = countOnes(randomDataBinary)
        dut.stream.poke(randomData.U)
        dut.res.expect(correctCount)
//        println(s"stream: ${dut.stream.peek().litValue.toInt.toBinaryString}; res : ${dut.res.peek().litValue} ")
      }
        dut.clock.step(1)
      }
    }

  "Test Decoder" should "pass" in {
    def checkBitstream(data: Int, stochastream:Boolean): String = {
      val res = data match {
        case 0 => "00000000"
        case 5 | 13 => if (stochastream)"11011001" else  "11111000"
        case 3 | 6 | 11 | 14 => if (stochastream)"11011011" else  "11111100"
        case 7 | 15 => if (stochastream)"11111011" else "11111110"
        case _ => "11111111"
      }
      //      Integer.parseInt(res, 2)
      res
    }
    test(new DecoderInfo(false)) { dut =>
      for (i <- 1 until 16) {
        dut.io.flint.poke(i.U)
        val res = checkBitstream(i,false)
        dut.io.streamIO.stream.expect(Integer.parseInt(res, 2).asUInt)
        println(s"i:${i} stream:${dut.io.streamIO.stream.peek().litValue.toInt.toBinaryString} , expect res :${res}")
        dut.clock.step(1)
      }
    }
  }

  "Test ParallelComputing" should "pass" in {
    test(new Top()) { dut =>
      for (i <- 1 until 16) {
        println(s"************Begin*************\n")

        val randomData_1 = 1+scala.util.Random.nextInt(((pow(2,4)-2).toInt))
        val randomData_2 = 1+scala.util.Random.nextInt(((pow(2,4)-2).toInt))
        dut.io.flint_act.poke(randomData_1.U)
        dut.io.flint_weight.poke(randomData_2.U)
//        dut.io.streamIO.stream.expect(Integer.parseInt(res, 2).asUInt)
        dut.clock.step(1)
        println(s"************End*************\n")
      }
    }
  }

}
