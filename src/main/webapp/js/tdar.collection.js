(function(console, $, TDAR){
    "use strict";

    console.log("tdar.collection.js");

    var tableInitialized = false;


    function _initializeDataTable() {
        TDAR.datatable.setupDashboardDataTable({
            isAdministrator: false,
            isSelectable: true,
            showDescription: false,
            selectResourcesFromCollectionid: $("#metadataForm_id").val(),
            "sDom" : "<'row-fluid'<'span6'l><'pull-right span6'r>>t<'row-fluid'<'span6'i><'span6'p>>"
            //rowSelectionCallback: function(id, obj, isAdded){
            //    console.log("rowSelection:: id:%s obj:%s isAdded:%s", id, obj, isAdded);
            //}
        });
        tableInitialized = true;
    }

    function _setupResourcePopup() {

        var $popupContainer = $('#divDataTablePopupContainer');
        var $dataTable = $("#resource_datatable");
        var selectedItems = [];

        $("#btnOpenModal").click(function() {

            $popupContainer.modal();
            console.log("modal");
        });

        $popupContainer.on("shown", function() {
            //DataTable can't properly initialize until visible,  so initialize upon first appearance
            if(!tableInitialized) {
                _initializeDataTable()
            }

        });

        $popupContainer.on("hidden", function() {
            console.log("hidden");
            console.log(arguments);
        });

    }

    function _resetDataTable(selector) {
        $("")
    }

    function _setupCurrentResourcesDataTable(selector) {

        var $dataTable = $(selector);
        var collectionId = $(selector).data("collectionid");



        var $dataTable = TDAR.datatable.registerLookupDataTable({
            tableSelector : selector,
            "bLengthChange" : true,
            "bFilter" : false,
            aoColumns : [
                {
                    "mDataProp": "id",
                    //fnRender: _cb,
                    bSortable:false
                },

                {
                    "mDataProp": "title",
                    //fnRender: _cb,
                    bSortable:false
                },

                {
                    "mDataProp": "resourceTypeLabel",
                    //fnRender: _cb,
                    bSortable:false
                },

                    //bug: datatable.net requires every column to be bound to an json property,  even derived columns (because reasons)
                    //      even worse, you can't re-use bound properties,  so we choose detailUrl because it isn't used already
                    //      (have I mentioned that this is a nightmare that I cannot wake up from?)
                {
                    mDataProp: "detailUrl",
                    bSortable: false,
                    sTitle: "action",
                    fnRender: function(obj, resourceId) {
                        console.log("fnrender", obj);
                        return "<button type='button' class='btn btn-mini btnRemoveResource' data-id='"+ obj.aData.id + "' >Remove</button>";
                        }
                }

            ],
            "sDom" : "<'row'<'span6'l><'pull-right span3'r>>t<'row'<'span4'i><'span5'p>>", // no text filter!
            "sAjaxSource" : '/lookup/resource?collectionId=' + collectionId + '&parentCollectionsIncluded=false',
            sAjaxDataProp : 'resources',
            "sScrollY" : "",
            "sScrollX" : "",
            "bScrollCollapse" : true
        });
    }


    TDAR.collection = {
        "setupResourcePopup": _setupResourcePopup,
        "resetDataTable": _resetDataTable,
        "setupCurrentResourcesDataTable": _setupCurrentResourcesDataTable
    };

})(console, jQuery, TDAR);