package main.scala.core

/**
 * literals like boolean, string, integer, and double in lisp
 */
sealed trait Atom {
  import langType._
  def inferType(env: Environment): langType = this match {
    case AtomBoolean(_) => boolean
    case AtomInt(_) => int
    case AtomDouble(_) => double
    case AtomString(_) => string
    case AtomIdentifier(x) => {
      env.lookUp(x) match {
        case Right(s) => s.inferType(env)
        case Left(s) => {
          if(!env.functionTable.contains(x)) throw new RuntimeException(s)
          else lambda
        }
      }
    }
    case _ => throw new RuntimeException(s"unable to infer type for atom $this")
  }
  
  /**
   * @return no identifier support yet
   */
  def stripValue = this match {
    case AtomInt(i) => i
    case AtomDouble(d) => d
    case AtomBoolean(b) => b
    case AtomString(s) => s
    case AtomIdentifier(n) => n
    case AtomLambda(v, b) => s"(lambda ($v) ($b))"
  }
  
  def arithSafeConversion = this match {
    case a@AtomDouble(_) => a
    case a@AtomInt(_) => a.toAtomDouble
    case _ => throw new RuntimeException(s"unable to recognized $this, should be an arithmetic identity")
  }
}

import langType._

/**
 * the boolean type here represented by 真 and 假
 */
case class AtomBoolean (value: Boolean) extends Atom {
  val typeInfo = boolean
  override def toString = this.value.toString()
}

case class AtomInt (value: Int) extends Atom {
  val typeInfo = int
  implicit def toAtomDouble: AtomDouble = AtomDouble(this.value.toDouble)
  implicit def toAtomString: AtomString = AtomString(this.value.toString)
  override def toString = this.value.toString()
}

case class AtomDouble (value: Double) extends Atom {
  val typeInfo = double
  implicit def toAtomString: AtomString = AtomString(this.value.toString)
  override def toString = this.value.toString()
}

case class AtomString (value: String) extends Atom {
  val typeInfo = string
  implicit def toAtomInt: AtomInt = AtomInt(this.value.toInt)
  implicit def toAtomDouble: AtomDouble = AtomDouble(this.value.toDouble)
  override def toString = this.value.toString()
}

case class AtomIdentifier(name: String) extends Atom {
  override def toString = "iden: " + this.name
}

case class AtomLambda(varName: String, body: Expression) extends Atom {
  override def toString = s"(lambda ($varName) ($body))"
  def toLambdaExpression = LambdaExpression(varName, body)
}

/*
/**
 * @return a closure strucutre of certain identifier
 */
case class AtomClosure (value: AtomIdentifier, env: Environment) extends Atom{
  def getValue = env.lookUp(value.name)
  override def toString = s"Closure with $value and $env"
}
*/