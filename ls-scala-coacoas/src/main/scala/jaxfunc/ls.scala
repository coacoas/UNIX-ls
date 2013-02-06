package jaxfunc

import java.io.File
import scala.Function1

/**
 * Typeclass trait to translate from A => String
 * 
 * @author bcarlson
 * @param <A>
 */
trait Show[A] {
  def show: String
}

object Files {
  def asFile(name: String) = new File(name)
  
  val list: File => Either[String, (File, Seq[File])] = f =>
    if (!f.exists) Left(s"Could not find ${f.getAbsolutePath}")
    else if (f.isDirectory) Right(f, f.listFiles())
    else Right(f, List(f))

  implicit class FileShow(val file: File) extends Show[File] {
    val show = s"${file.getName()}"
  }

  implicit class FileSeqShow(val file: (File, Seq[File])) extends Show[(File, Seq[File])] {
    def show = file match {
      case (parent, children) if parent.isDirectory() =>
        s"${parent.getAbsolutePath}:\n ${children.map(_.show).mkString("\n")}"
      case (f, _) => f.show
    }
  }

  implicit class LsShow(val f: Either[String, (File, Seq[File])])
    extends Show[Either[String, (File, Seq[File])]] {
    def show = f match {
      case Left(err) => err
      case Right(listing) => listing.show
    }
  }
}

object ls extends App {
  import Files._

  val (opts, fileArgs) = args.partition(_.startsWith("-"))
  val fileNames = if (fileArgs.isEmpty) Array(".") else fileArgs
  
  // Showing off chaining methods as well as function composition
  val listings = fileNames.map(list compose asFile).map(_.show)

  println(listings.mkString("\n"))
}
