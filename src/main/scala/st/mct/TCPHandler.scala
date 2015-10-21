package st.mct

import java.net.InetSocketAddress

import scala.util.Random

import akka.actor._
import akka.io.Tcp
import akka.io.Tcp.{ PeerClosed, Received, Write }
import akka.util.ByteString

object TCPHandlerStates {
  sealed trait State
  case object Idle extends State
  case object Waiting extends State
  case object Uploading extends State
  case object Downloading extends State
}

object TCPHandlerMessages {
  final val Start    = "HELLO FROM (.+)".r
  final val Done     = "END".r
  final val Upload   = "".r
  final val Download = "DOWNLOAD ([0-9]+)".r
}

object TCPHandlerTasks {

  final def doDownload(n: String) = {
    val bytes = Array.fill[Byte](n.toInt)(0)
    Random.nextBytes(bytes)
    ByteString(bytes)
  }

}

object TCPHandler {
  def props(remote: InetSocketAddress, connection: ActorRef) =
    Props(new TCPHandler(remote, connection))
}

class TCPHandler(remote: InetSocketAddress, connection: ActorRef) extends Actor with ActorLogging {
  import TCPHandlerMessages._
  import TCPHandlerStates._
  import TCPHandlerTasks._

  // We need to know when the connection dies without
  // sending a `Tcp.ConnectionClosed`
  context.watch(connection)

  var state: State = Idle

  private def process(text: String) = text match {
    case Start(greeting) ⇒ {
      log.info(s"Received handshake message from [$greeting], waiting for command...")
      state = Waiting
    }
    case Done() ⇒ {
      log.info(s"Received breakup message from [$remote]")
      state = Idle
    }
    case Download(n) ⇒ {
      log.info(s"Received download message, responding download request with [$n] bytes")
      sender ! Write(doDownload(n))
      state = Downloading
    }
    case _ ⇒
      log.error(s"Error: I don't know how to handle message [$text], ignored")
  }

  override def receive = {
    case Received(data) ⇒
      val text = data.utf8String.trim
      process(text)

    case PeerClosed ⇒
      log.info(s"Stopping, remote peer [$remote] closed connection")
      context stop self

    case _: Tcp.ConnectionClosed ⇒
      log.info(s"Stopping, remote connection [$remote] closed")
      context stop self

    case Terminated(`connection`) ⇒
      log.info(s"Stopping, remote connection [$remote] died")
      context stop self
  }
}
