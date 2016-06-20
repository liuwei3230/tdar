Dear tDAR Admin:
<#list toExpire![]>
The following files will be un-embargoed tomorrow:
<#items as c>
 - ${c.file.filename} (${c.file.id?c}):  ${c.resource.title} (${c.resource.id?c})
   ${baseUrl}${c.resource.detailUrl} (${c.resource.submitter.properName})
</#items></#list>

<#list expired![]>
The following files have been unembargoed:
<#items as c>
 - ${c.file.filename} (${c.file.id?c}):  ${c.resource.title} (${c.resource.id?c})
   ${baseUrl}${c.resource.detailUrl} (${c.resource.submitter.properName})
</#items></#list>
