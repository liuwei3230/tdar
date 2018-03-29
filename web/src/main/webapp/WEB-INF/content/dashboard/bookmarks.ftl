<#escape _untrusted as _untrusted?html>
<#import "/WEB-INF/macros/resource/list-macros.ftl" as rlist>
<#import "common-dashboard.ftl" as dash />
<#import "/WEB-INF/macros/resource/edit-macros.ftl" as edit>
<#import "/WEB-INF/macros/resource/view-macros.ftl" as view>
<#import "/WEB-INF/macros/search-macros.ftl" as search>
<#import "/WEB-INF/macros/resource/common-resource.ftl" as commonr>
<#import "/WEB-INF/macros/common.ftl" as common>
<#import "/${config.themeDir}/settings.ftl" as settings>

<head>
    <title>${authenticatedUser.properName}'s Dashboard</title>
    <meta name="lastModifiedDate" content="$Date$"/>

</head>


<div id="titlebar" parse="true">
<form>
    <h1>Dashboard &raquo; <span class="red">My Bookmarks</span></h1>
<div class="row">
<div class="span9">
<b>selectize</b>
<input class="selectizeajax" type="text" />
</div>
</div>


<div class="row">
<div class="span9">
<b>select2</b>
<select class="js-data-example-ajax"></select>
</div>
</div>



<div class="row">
<div class="span9">
<b>select2 (tags)</b>
<select class="js-data-example-ajaxmulti" multiple></select>
</div>
</div>





<div class="row">
<div class="span9">
<b>selectize (tags)</b>
<input class="selectize-ajaxmulti" />
<script>
$(document).ready(function() {
$('.js-data-example-ajaxmulti').select2({
  tags: true,
  ajax: {
    url: '/api/lookup/keyword',
    dataType: 'jsonp',
     data: function (params) {
      return {
      	keywordType: 'GeographicKeyword',
        term: params.term
      }
      },
      processResults: function (data) {
      // Tranforms the top-level key of the response object from 'items' to 'results'
      var results = [];
      data.items.forEach(function(r) {
      results.push({id:r.id, text:r.label});
      });
      return {
        results: results
      };
    }
    // Additional AJAX parameters go here; see the end of this chapter for the full code of this example
  }
});

$('.js-data-example-ajax').select2({
  ajax: {
    url: '/api/lookup/resource',
    dataType: 'jsonp',
     data: function (params) {
      return {
        term: params.term
      }
      },
      processResults: function (data) {
      // Tranforms the top-level key of the response object from 'items' to 'results'
      var results = [];
      data.resources.forEach(function(r) {
      results.push({id:r.id, text:r.title});
      });
      return {
        results: results
      };
    }
    // Additional AJAX parameters go here; see the end of this chapter for the full code of this example
  }
});
    $('.selectize-ajaxmulti').selectize({
        "delimiter": ',',
        "valueField": 'label',
        "labelField": 'label',
        "searchField": 'term',
        "persist": true,
		"createOnBlur": true,

        "render": {
            option: function(item, escape) {
	        console.log(item);
	            if (item.name != undefined) {
	                return '<div>' + '<span class="title">' + '<span class="sel-name">' + escape(item.name) + '</span>' + '</span>' + '</div>';
	            }
	            if (item.label != undefined) {
	                return '<div>' + '<span class="title">' + '<span class="sel-label">' + escape(item.label) + '</span>' + '</span>' + '</div>';
	            }
            }
        },
        "create": true,

        load: function(query, callback) {
        console.log('load');
            if (!query.length) return callback();
            $.ajax({
                url: '/api/lookup/keyword',
                dataType: 'jsonp',
                data: {
                    keywordType: 'GeographicKeyword',
                    term: query
                },
                error: function() {
                    callback();
                },
                success: function(res) {
                    callback(res['items']);
                }

            });
        }

    });

//        "delimiter": ',',
    $('.selectizeajax').selectize({
        "valueField": 'id',
        "labelField": 'title',
        "searchField": 'term',
        "persist": true,
        "maxItems": 1,
        "showCreate":false,
		"createOnBlur": false,

        render: {
            option: function(item, escape) {
	        console.log(item);
	            if (item.title != undefined) {
	                return '<div>' + '<span class="title">' + '<span class="sel-name">' + escape(item.title) + '</span>' + '</span>' + '</div>';
	            }
	            if (item.label != undefined) {
	                return '<div>' + '<span class="title">' + '<span class="sel-label">' + escape(item.label) + '</span>' + '</span>' + '</div>';
	            }
            }
        },
        create: true,

        load: function(query, callback) {
     	   console.log('load', callback);
            if (!query.length) return callback();
            //var self = this;
            $.ajax({
                url: '/api/lookup/resource',
                dataType: 'json',
                type:'GET',
                data: {
                    term: query
                },
                error: function() {
                    callback();
                },
                success: function(res) {
                	console.log(res['resources'].length);
                	var results = res['resources'];
                    callback(results);
//					self.addOption(results);
//					console.log(self.isFocused , !self.isInputHidden);
//					self.refreshOptions(true);

                }

            });
        }

    });


});

</script>
</div>
</div>

</div>
<div class="row">
<div class="span2">
    <@dash.sidebar current="bookmarks" />
</div>
<div class="span10">
    <@bookmarksSection />
        </div>
</div>

</div>


</div>

    <#macro moremenu>
    <div class="moremenu pull-right">
        <div class="btn-group">
            <button class="btn btn-mini">View</button>
            <button class="btn btn-mini">Edit</button>
            </div>
    </div>

    </#macro>


    <#macro bookmarksSection>
        <div id="bookmarks">
            <#if ( bookmarkedResources?size > 0)>
                <@rlist.listResources resourcelist=bookmarkedResources sortfield='RESOURCE_TYPE' listTag='ol' headerTag="h3" titleTag="b" />
            <#else>
            <h3>Bookmarked resources appear in this section</h3>
            Bookmarks are a quick and useful way to access resources from your dashboard. To bookmark a resource, click on the star <i class="icon-star"></i> icon next to any resource's title.
            </#if>
        </div>
    </#macro>

<script>
    $(document).ready(function () {
        TDAR.notifications.init();
        TDAR.common.collectionTreeview();
        $("#myCarousel").carousel('cycle');
    });
</script>



</#escape>