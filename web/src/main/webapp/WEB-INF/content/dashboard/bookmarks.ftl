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
    <!-- Include base CSS (optional) -->
<!-- Include Choices CSS -->
<link rel="stylesheet" href="/components/choices.js/assets/styles/css/choices.min.css">
<!-- Include Choices JavaScript -->
<script src="/components/choices.js/assets/scripts/dist/choices.min.js"></script>

</head>


<div id="titlebar" parse="true">
    <h1>Dashboard &raquo; <span class="red">My Bookmarks</span></h1>

</div>
<div class="row">
<div class="span2">
    <@dash.sidebar current="bookmarks" />
</div>

<div class="span10">
<form>
<b>selectize</b>
<input class="selectizeajax" type="text"  autocomplete="off" />

<br/><br/>
<b>select2</b>
<select class="js-data-example-ajax"></select>


<br/><br/>

<b>select2 (tags)</b>
<select class="js-data-example-ajaxmulti" multiple></select>


<br/><br/>

<b>selectize (tags)</b>
<input class="selectize-ajaxmulti" />



<b>choices (single)</b>
<select class="choicesajax" multiple></select>



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
        "valueField": 'id',
        delimeter:',',
        "labelField": 'name',
        "searchField": 'name',
        "persist": true,
        "maxItems": 100,
        "showCreate":false,
        "createOnBlur": false,

        render: {
            option: function(item, escape) {
            console.log(item);
                if (item.title != undefined) {
                    return '<div>' + '<span class="title">' + '<span class="sel-name">' + escape(item.title) + '</span>' + '</span>' + '</div>';
                }
                if (item.name != undefined) {
                    return '<div>' + '<span class="title">' + '<span class="sel-name">' + escape(item.name) + '</span>' + '</span>' + '</div>';
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
            var self = this;
            $.ajax({
                url: '/api/lookup/institution',
                dataType: 'json',
                type:'GET',
                data: {
                    institution: query
                },
                error: function() {
                    callback();
                },
                success: function(res) {
                    console.log(res['institutions'].length);
                    var results = res['institutions'];
                    callback(results);


                }

            });
        }

    });

//        "delimiter": ',',
    $('.selectizeajax').selectize({
        "valueField": 'id',
        "labelField": 'name',
        "searchField": 'name',
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
                if (item.name != undefined) {
                    return '<div>' + '<span class="title">' + '<span class="sel-name">' + escape(item.name) + '</span>' + '</span>' + '</div>';
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
            var self = this;
            $.ajax({
                url: '/api/lookup/institution',
                dataType: 'json',
                type:'GET',
                data: {
                    institution: query
                },
                error: function() {
                    callback();
                },
                success: function(res) {
                    console.log(res['institutions'].length);
                    var results = res['institutions'];
                    callback(results);


                }

            });
        }

    });





 var singleXhrRemove = new Choices('.choicesajax', {
        removeItemButton: true,
      }).ajax(function(callback) {
      console.log('hi');
                  $.ajax({
                url: '/api/lookup/institution?minLookupLength=0&recordsPerPage=1000',
                dataType: 'json',
                type:'GET',
                error: function() {
                    callback();
                },
                success: function(res) {
                    console.log(res['institutions'].length);
                    var results = res['institutions'];
                    callback(results,'name','name');

                }
            });


    });
});


</script>
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