TDAR.selectize = {};
TDAR.selectize = (function() {
    "use strict";

    var _getIdInputName = function(nme) {
        var _name = nme;
        
        // if we're using 'dotted' syntax, then set the Id attribute
        if (_name.indexOf(".") > 0) {
            _name = _name.substring(0, _name.lastIndexOf(".") + 1);
            this.$input.tdarIdField = _name + 'id';
        } else if (_name.endsWith("]")) {
            _name = [_name.slice(0, _name.lastIndexOf("[")), "Id", _name.slice(_name.lastIndexOf("["))].join('');

        } else {
            _name = _name + 'Id';
        }
        return _name;
    }

    var _generic = function(url, group, queryField, valueField, labelField, renderFunction, extra) {
        // needs to be managed prior to onIntialize();
        var tdarRenderFunction = function(item, escape) {
            return '<div>' + '<span class="title">' + '<span class="name">' + escape(item.name) + '</span>' + '</span>' + '</div>';
        };
        if (renderFunction != undefined && typeof renderFunction === 'function') {
            tdarRenderFunction = renderFunction;
        }


        var opts = {
            valueField: valueField,
            labelField: labelField,
            searchField: 'name',
            create: true,
            maxItems: 1,
            createOnBlur: true,
            onChange(value) {
                /** 
                 * When we set a value, set the hidden ID field too
                 */
                var id = '';

                var _id = this.options[value];
                if (_id != undefined) {
                    id = _id.id;
                }

                console.log("change:", value, id);
                $("#" + this.$input.tdarIdField).val(id);
            },
            onClear() {
                console.log("clear");
                $("#" + this.$input.tdarIdField).clear();
            },
            onInitialize() {
                var $inp = $(this.$input);
                if (extra != undefined) {
                    this.tdarQueryExtra = extra;
                } else {
                    this.tdarQueryExtra = {};
                }

                var _name = _getIdInputName($inp.attr("name"));
                this.$input.tdarIdField = _name;


                // create a hidden element for our Id if it doesn't exist 
                if ($(this.$input.tdarIdField).length == 0) {
                    $inp.after($('<input>').attr({
                        type: 'hidden',
                        name: this.$input.tdarIdField,
                        id: this.$input.tdarIdField
                    }));
                }
            },
            render: {
                option: tdarRenderFunction
            },

            load: function(query, callback) {
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

        var opts = _generic("/api/lookup/institution", "institutions", "institution", "name", "name", undefined, {});
        $(selector).selectize(opts);
    }

    return {
        institutionLookup: _institutionLookup
    };
})();
