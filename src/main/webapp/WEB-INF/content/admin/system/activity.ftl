<#escape _untrusted as _untrusted?html >
<#import "/WEB-INF/macros/resource/view-macros.ftl" as view>
<head>
<title>Administrator Dashboard: Recent Activity</title>
<meta name="lastModifiedDate" content="$Date$"/>
</head>

<h2>Recent System Activity</h2>
<hr/>
<@s.actionerror />

<h3>Recent Actions</h3>
<table class="tableFormat zebraColors">
<tr>
	<th>browser</th><th>count</th>
</tr>
<tr>
<#list counters?keys as count>
<#if count?has_content>
 <tr>
 	<td>${count}</td>
 	<td>${counters.get(count)}</td>
  </tr>
</#if>
</#list>
</tr>
</table>
<br/>
<table class="tableFormat zebraColors">
<tr>
	<th>date</th><th>total time (ms)</th><th>request</th>
</tr>
<tr>
<#list activityList as activity>
 <tr>
 	<td>${activity.startDate?datetime}</td>
 	<td>${activity.totalTime!default("-")}</td>
 	<td width=550>${activity.name!""}</td>
  </tr>
</#list>
</tr>
</table>
<style>
pre {white-space: pre-line;
width: 550px;
}}
</style>
<h3>Scheduled Processes Currently in the Queue</h3>
<#if scheduledProcessesEnabled??>
<ol>
<#list scheduledProcessQueue as process>
 <li>${process} - current id: ${process.lastId?c}</li>
</#list>
</ol>
<#else>
 Scheduled Processes are not enabled on this machine
</#if>

<h3>Configured Scheduled Processes</h3>
<#if scheduledProcessesEnabled??>
<ol>
<#list allScheduledProcesses as process>
 <li>${process}</li>
</#list>
</ol>
<#else>
 Scheduled Processes are not enabled on this machine
</#if>

<h3>Hibernate Statistics</h3>
${sessionStatistics}


</#escape>
