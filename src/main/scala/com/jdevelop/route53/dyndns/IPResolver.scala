package com.jdevelop.route53.dyndns

/**
 * User: Eugene Dzhurinsky
 * Date: 7/17/13
 */
trait IPResolver {

  val resolverURI: String

  def getMyIPAddress: String = io.Source.fromURL(resolverURI).mkString

}
