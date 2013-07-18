package com.jdevelop.route53.dyndns

import com.typesafe.config.ConfigFactory
import java.io.File
import java.util.concurrent.{TimeUnit, Executors}
import com.amazonaws.services.route53.{AmazonRoute53Client, AmazonRoute53}
import com.amazonaws.auth.BasicAWSCredentials
import org.slf4j.LoggerFactory
import org.apache.log4j.{Level, BasicConfigurator, Logger}

/**
 * User: Eugene Dzhurinsky
 * Date: 7/17/13
 */
object Main {

  val LOGNAME = "MAIN"

  BasicConfigurator.configure()
  val rootLogger = Logger.getRootLogger
  rootLogger.setLevel(Level.ERROR)
  val LOG = LoggerFactory.getLogger(LOGNAME)
  val innerLogger = Logger.getLogger(LOGNAME)

  def main(args: Array[String]) {
    args.filter(_ == "-v").length match {
      case 1 => innerLogger.setLevel(Level.DEBUG)
      case 2 => rootLogger.setLevel(Level.DEBUG)
      case _ =>
    }
    val cfgFile = new File(sys.env("HOME"), ".dyndns.rc")
    LOG.debug("Using config file: {}", cfgFile)
    require(cfgFile.exists(), "Must have configuration file: " + cfgFile.getAbsolutePath)
    val config = ConfigFactory.parseFile(cfgFile)
    val executor = Executors.newSingleThreadScheduledExecutor
    val updateInterval = config.getLong("route53.zone.updateInterval")
    LOG.debug("Schedule execution with interval {} seconds", updateInterval)
    executor.scheduleWithFixedDelay(new Runnable with IPResolver with RecordUpdater {

      def run() {
        LOG.debug("Getting IP address from {}", resolverURI)
        val ipAddress = getMyIPAddress
        LOG.debug("IP address is '{}'", ipAddress)
        LOG.debug("Update IP address for subdomain {} with TTL {}", subdomain, TTL)
        updateRecord(subdomain, ipAddress)
        LOG.debug("Zone update done")
      }

      val resolverURI: String = config.getString("route53.resolver.uri")

      val route53: AmazonRoute53 = new AmazonRoute53Client(new BasicAWSCredentials(
        config.getString("route53.aws.accessKey"),
        config.getString("route53.aws.secretKey")
      ))

      val zoneId: String = config.getString("route53.zone.id")

      val subdomain = config.getString("route53.zone.subdomain")

      val TTL: Long = config.getLong("route53.zone.ttl")
    }, 1, updateInterval, TimeUnit.SECONDS)
  }

}