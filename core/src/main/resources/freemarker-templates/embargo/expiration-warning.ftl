Dear ${submitter.properName},

The following files are marked as "embargoed" in ${siteAcronym}. That embargo will expire tomorrow.
At that time, the embargo will be automatically removed.
  
<#list files as c>
	- ${c.file.filename}:  ${c.resource.title} (${c.resource.id?c}) - ${baseUrl}${c.resource.detailUrl}
</#list>