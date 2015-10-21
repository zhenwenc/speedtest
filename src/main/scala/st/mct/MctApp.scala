package st.mct

import java.net.InetSocketAddress

import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorSystem, Props }

import st.utils.Logging

object MctApp extends App with Logging {
  implicit val system = ActorSystem("mct-system")

  system.registerOnTermination {
    log.info("Actor system has been shut down.")
  }
  sys.addShutdownHook(system.shutdown)

  val config = ConfigFactory.load()
  val host = config.getString("mct.host")
  val port = config.getInt("mct.port")

  val endpoint = new InetSocketAddress(host, port)
  system.actorOf(Props(new TCPServer(endpoint)), "mct-tcp-service")
}
