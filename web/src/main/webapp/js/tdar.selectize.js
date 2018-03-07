TDAR.selectize = {};
TDAR.selectize = (function() {
    "use strict";

    /**
     * FIXME: On focus loss, input reverts to wrong spot on page
     */
    
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
     * find the next 'focusable element'
     * ignore things that have a tabIndex == -1 unless they have an attribute 'selectizeFor'
     * 
     */
    var _focusOn = function(existingName) {
        setTimeout(function() {
            var focusable = $(':focusable');
            var seen = false;
            for (var i= 0; i < focusable.length; i++) {
                var $val = $(focusable[i]);
                if ($val.attr('tabindex') == -1 && $val.attr("selectizeFor") == undefined) { continue;}
                console.log(existingName, $val.name, $val.attr("selectizeFor"), $val);
                if ($val.name == existingName || $val.attr("selectizeFor") == existingName) {
                    console.log($val, $val.name == existingName);
                    seen = true;

                } else {
                    if (seen == true) {
                        console.log('focusing on', $(focusable[i]));
                        $(focusable[i]).focus();
                        break;
                    }
                }
            };
          },100);
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
    var _generic = function(url, group, queryField, valueField, labelField, renderFunction, extra, showCreate) {
        // needs to be managed prior to onIntialize();
        
        // default render function
        var tdarRenderFunction = function(item, escape) {
            if (item.name != undefined) {
                return '<div>' + '<span class="title">' + '<span class="sel-name">' + escape(item.name) + '</span>' + '</span>' + '</div>';
            }
            if (item.label != undefined) {
                return '<div>' + '<span class="title">' + '<span class="sel-label">' + escape(item.label) + '</span>' + '</span>' + '</div>';
            }
        };
        
        // if render function is passed in, then use that
        if (renderFunction != undefined && typeof renderFunction === 'function') {
            tdarRenderFunction = renderFunction;
        }


        var opts = {
            "valueField": valueField,
            "labelField": labelField,
            "searchField": valueField,
            "create": showCreate,
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

                console.trace("change:", value, id);
                $("#" + this.$input.tdarIdFieldId).val(id);
                _focusOn($(this.$control_input).attr("selectizeFor"));

            },
            onClear : function() {
                /**
                 * clear the value and ID
                 */
                console.trace("clear");
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

                var originalInputName = $inp.attr("name");
                
                var _name = _getIdInputName(originalInputName);
                this.$input.tdarIdFieldName = _name;
                this.$input.tdarIdFieldId = _getIdFromName(_name);
                
                // remove the wrapper on repeat
                this.$wrapper.addClass("repeat-row-remove");
                
                // add a selector for the input
                $(this.$control_input).attr("selectizeFor",originalInputName);
                $(this.$control_input).keydown(function(e) {
                    var code = e.keyCode || e.which;

                    if (code === 9) {
                        _focusOn(originalInputName);
                    }
                });
                // create a hidden element for our Id if it doesn't exist
                var ids = $("input[name=\'" +this.$input.tdarIdFieldName+"\']");
                if (ids.length == 0) {
                    $inp.before($('<input>').attr({
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
            var opts = _generic("/api/lookup/institution", "institutions", "institution", "name", "name", undefined, {}, true);
            _apply(selector,opts);
        }
    }

    var _apply = function(selector, opts) {
        $(selector).each(function(i,sel) {
            var $sel = $(sel);
//             opts['dropdownParent'] = $sel.parent()[0];
            $sel.selectize(opts);
            $(sel).blur(function(e) {
               var existing = $(this).attr('selectizeFor');
               _focusOn(existing);
            });
        });
    }
    
    
    var _collectionLookup = function(selector, permission, create) {
        if ($(selector).length > 0 ){
            var opts = _generic("/api/lookup/collection", "collections", "term", "name", "name", undefined, {permission: permission}, create);
            _apply(selector,opts);
        }
    }

    var _keywordLookup = function(selector, type) {
        if ($(selector).length > 0 ){
            var opts = _generic("/api/lookup/keyword", "items", "term", "label", "label", undefined, {keywordType: type}, true);
            _apply(selector,opts);
        }
    }
    
    /**
     * Generic method for managing repeatrow initialization and binding, pass in a referecne to the initialization selector, 
     * the table selector, the extra parameter 
     */
    var _lookupWithRepeatRow = function(selector, method, table, extra) {
        var $sel = $(selector); 
        if ($sel.length > 0 ){
            method(selector, extra);
            var $table = $(table);
            $table.on("repeatrowadded", function(e,a,row) {
                method("#" + row.id + " .selectize", extra);
            });
            $table.on("repeatrowclear", function(e,$row) {
                var sel = $row.find(".selectize").data('sel');
                if (sel != undefined) {
                    sel.clear();
                }
            });

        }
    }

    var _keywordLookupWithRepeatRow = function(selector, table, type) {
        _lookupWithRepeatRow(selector, _keywordLookup, table, type);
    }
 
    return {
        institutionLookup: _institutionLookup,
        keywordLookup: _keywordLookup,
        keywordLookupWithRepeatRow: _keywordLookupWithRepeatRow,
        collectionLookup: _collectionLookup
    };
})();
