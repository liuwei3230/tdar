(function(console, $, TDAR){
    console.log("tdar.collection.js");


    function _setupResourcePopup() {
        $(function() {

            var $popupContainer = $('#divDataTablePopupContainer');
            var $dataTable = $("#resource_datatable");
            var tableInitialized = false;

            $("#btnOpenModal").click(function() {

                $popupContainer.modal();
                console.log("modal");
            });

            $popupContainer.on("shown", function() {
                //DataTable can't properly initialize until visible,  so initialize upon first appearance
                if(tableInitialized) {return;}

                TDAR.datatable.setupDashboardDataTable({
                    isAdministrator: false,
                    isSelectable: true,
                    showDescription: false,
                    selectResourcesFromCollectionid: $("#metadataForm_id").val(),
                    "sDom" : "<'row-fluid'<'span6'l><'pull-right span6'r>>t<'row-fluid'<'span6'i><'span6'p>>"
                });
                tableInitialized = true;
            });

            $popupContainer.on("hidden", function() {
                console.log("hidden");
            })

        })


    }


    function _resetDataTable(selector) {
        $("")
    }


    TDAR.collection = {
        "setupResourcePopup": _setupResourcePopup,
        "resetDataTable": _resetDataTable
    };

})(console, jQuery, TDAR);