package main.scala.engine

import main.scala.interpreter._
import main.scala.core._
import scala.io._
import main.scala.util._

object REPL {
  import langType._
  
  private var env0 = Environment()
  private val replIns = "iota-lang> "
  private val savedIns = "./repl.iota"
  
  def initiate = {
    println("welcome to iota-lang, type in expressions for evaluation or"+
        " `exit` to quit iota-repl")
    def loop(i: Int): Unit = {
      printInstr
      val cmd = StdIn.readLine()
      diskOperator.writeToLocalDisk(savedIns, cmd+"\n\r", true)
      if(cmd.equals("exit")) Unit
      else if(cmd.equals("\\lookup")) {
        env0.replPrint
        loop(i)
      } else {
        printEvaled(i, evaluateSingle(cmd))
        loop(i+1)
      }
    }; loop(0)
  }
  
  private def printInstr = print(replIns)
  
  private def printEvaled(i: Int, r: Tuple2[Atom, langType]) = {
    if(r._2 == unit) {
      val looked = env0.lookUp(r._1.stripValue.toString())
      looked match {
        case Right(a) => println(r._1.stripValue + ": " + a.inferType(env0) + " = " + a.stripValue)
        case Left(_) => {
          if(env0.functionTable.contains(r._1.stripValue.toString())) println(r._1.stripValue + ": lambda expression")
          else println("define expression failed")
        }
      }
    }
    else {
      val resName = "res"+i
      env0.extendEnvironment(resName, r._1)
      if(r._2 == langType.lambda) println(resName+": "+r._1.inferType(env0)+" = "+r._1.stripValue)
      else println(resName+": "+r._2+" = "+r._1.stripValue)
    }
  }
  
  private def evaluateSingle(cmd: String) = {
    val repl = Parser(Lexer(Macro(cmd).initializeRaw()).getTokens(), env0).getSExpression()
    (Evaluator(repl).eval(repl.head, env0), repl.head.inferType(env0))
  }
}