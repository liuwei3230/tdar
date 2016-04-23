Dear ${submitter.properName},

The following files were marked as "embargoed" in ${siteAcronym}. That embargo has expired and
the embargo has been removed.  All ${siteAcronym} users can now download it.

<#list files as c>
	- ${c.file.filename}:  ${c.resource.title} (${c.resource.id?c}) - ${baseUrl}${c.resource.detailUrl}
</#list>