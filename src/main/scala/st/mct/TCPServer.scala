package st.mct

import java.net.InetSocketAddress

import akka.actor.{ Actor, ActorLogging }
import akka.io.Tcp._
import akka.io.{ IO, Tcp }

import st.mct.TCPHandler._


class TCPServer(endpoint: InetSocketAddress) extends Actor with ActorLogging {
  import context.system

  IO(Tcp) ! Bind(self, endpoint)

  override def receive = {
    case b @ Bound(localAddress) ⇒
      log.info(s"Bound [mct-tcp-service] on ${endpoint.getHostName}:${endpoint.getPort}}]")
    case CommandFailed(_: Bind) ⇒
      log.debug("Stopping service, command failed")
      context stop self
    case c @ Connected(remote, local) ⇒
      val handler = context.actorOf(props(remote, sender))
      sender ! Register(handler)
  }
}
