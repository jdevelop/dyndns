package com.jdevelop.route53.dyndns

import com.amazonaws.services.route53.AmazonRoute53
import com.amazonaws.services.route53.model._

/**
 * User: Eugene Dzhurinsky
 * Date: 7/17/13
 */
trait RecordUpdater {

  val route53: AmazonRoute53

  val zoneId: String

  val TTL: Long

  def updateRecord(subdomain: String, ipAddress: String) {
    import collection.JavaConversions._
    val recordSets = route53.listResourceRecordSets(new ListResourceRecordSetsRequest(zoneId))
    val update = recordSets.getResourceRecordSets.find(_.getName == (subdomain + ".")).map {
      // first of all we need to delete our zone if one exists
      r => List(new Change(ChangeAction.DELETE, {
        val change = new ResourceRecordSet(subdomain, RRType.A)
        change.setTTL(r.getTTL)
        change.setResourceRecords(
          List(
            new ResourceRecord(r.getResourceRecords.get(0).getValue)
          )
        )
        change
      }))
    }.getOrElse(List()) :+
      // then we have to add new zone
      new Change(ChangeAction.CREATE, {
        val change = new ResourceRecordSet(subdomain, RRType.A)
        change.setTTL(TTL)
        change.setResourceRecords(
          List(
            new ResourceRecord(ipAddress)
          )
        )
        change
      })
    route53.changeResourceRecordSets(
      new ChangeResourceRecordSetsRequest(zoneId, new ChangeBatch(update)
      )
    )
  }

}