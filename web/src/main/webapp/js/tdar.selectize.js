TDAR.selectize = {};
TDAR.selectize = (function() {
    "use strict";

    /**
     * ID fields can't have all characters, so we need to clean
     */
    var _getIdFromName = function(name) {
        if (name == undefined) {
            return undefined;
        }
        return name.replace(/[\[\]\.]/ig , "_");

    }
    
    /**
     * try and construct the same field structure with an ID. 
     * 
     * 1. if we're using myfield.name then use a.id
     * 2. if we're using array syntax add myfieldName[0] then use myfieldId[0]
     * 3. otherwise append id as myfieldname as myfieldnameId
     */
    var _getIdInputName = function(nme) {
        if (nme == undefined) {
            return undefined;
        }

        var _name = nme;
        // if we're using 'dotted' syntax, then set the Id attribute
        if (_name.indexOf(".") > 0) {
            _name = _name.substring(0, _name.lastIndexOf(".") + 1) + 'id';
        } else if (_endsWith(_name, "]")) {
            _name = [_name.slice(0, _name.lastIndexOf("[")), "Id", _name.slice(_name.lastIndexOf("["))].join('');

        } else {
            _name = _name + 'Id';
        }
               
        return _name;
    }
    
    /**
     * Simple string end's with
     */
    var _endsWith = function (str, suffix) {
        return str.indexOf(suffix, str.length - suffix.length) !== -1;
    }

    /**
     * generic initialization
     */
    var _generic = function(url, group, queryField, valueField, labelField, renderFunction, extra) {
        // needs to be managed prior to onIntialize();
        
        // default render function
        var tdarRenderFunction = function(item, escape) {
            return '<div>' + '<span class="title">' + '<span class="name">' + escape(item.name) + '</span>' + '</span>' + '</div>';
        };
        
        // if render function is passed in, then use that
        if (renderFunction != undefined && typeof renderFunction === 'function') {
            tdarRenderFunction = renderFunction;
        }


        var opts = {
            "valueField": valueField,
            "labelField": labelField,
            "searchField": 'name',
            "create": true,
            "maxItems": 1,
            "createOnBlur": true,
            onChange: function(value) {
                /** 
                 * When we set a value, set the hidden ID field too
                 */
                var id = '';

                var _id = this.options[value];
                if (_id != undefined) {
                    id = _id.id;
                }

                console.log("change:", value, id);
                $("#" + this.$input.tdarIdFieldId).val(id);
            },
            onClear : function() {
                /**
                 * clear the value and ID
                 */
                console.log("clear");
                $("#" + this.$input.tdarIdFieldId ).val('');
            },
            onInitialize : function() {
                /**
                 * setup... which in this case creates the id field link
                 */
                var $inp = $(this.$input);
                
                $inp.data('sel',this);
                if ($inp == undefined) {
                    return;
                }
                if (extra != undefined) {
                    this.tdarQueryExtra = extra;
                } else {
                    this.tdarQueryExtra = {};
                }

                var _name = _getIdInputName($inp.attr("name"));
                this.$input.tdarIdFieldName = _name;
                this.$input.tdarIdFieldId = _getIdFromName(_name);
                
                this.$wrapper.addClass("repeat-row-remove");

                // create a hidden element for our Id if it doesn't exist
                var ids = $("input[name=\'" +this.$input.tdarIdFieldName+"\']");
                if (ids.length == 0) {
                    $inp.after($('<input>').attr({
                        type: 'hidden',
                        name: this.$input.tdarIdFieldName,
                        id: this.$input.tdarIdFieldId
                    }));
                } else {
                    console.log("existing val:", ids[0].id);
                    this.$input.tdarIdFieldId = ids[0].id;
                }
            },
            render: {
                option: tdarRenderFunction
            },

            load: function(query, callback) {
                /**
                 * load data from the ajax query
                 */
                if (!query.length) {
                    return callback();
                }
                var data = $.extend({}, this.tdarQueryExtra);
                data[queryField] = query;

                $.ajax({
                    url: url,
                    type: 'GET',
                    data: data,
                    error: function() {
                        callback();
                    },
                    success: function(res) {
                        callback(res[group]);
                    }
                });
            }
        };
        return opts;
    }

    var _institutionLookup = function(selector) {
        if ($(selector).length > 0 ){
            var opts = _generic("/api/lookup/institution", "institutions", "institution", "name", "name", undefined, {});
            _apply(selector,opts);
        }
    }

    var _apply = function(selector, opts) {
        $(selector).each(function(i,sel) {
            console.log(sel);
            $(sel).selectize(opts);
        });
    }
    
    
    var _collectionLookup = function(selector, permission) {
        if ($(selector).length > 0 ){
            var opts = _generic("/api/lookup/collection", "collections", "collection", "name", "name", undefined, {permission: permission});
            _apply(selector,opts);
        }
    }
 
    return {
        institutionLookup: _institutionLookup,
        collectionLookup: _collectionLookup
    };
})();
