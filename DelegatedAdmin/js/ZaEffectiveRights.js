ZaEffectiveRights = function (grantee) {
    ZaItem.call (this, "ZaEffectiveRights") ;
    this.type = "ZaEffectiveRights" ;
    
    if (!grantee) return ;
    this.grantee = {
        id: grantee.id ,
        name: grantee.name 
    }

    if (grantee.type == ZaItem.ACCOUNT) {
        this.grantee.type = ZaGrant.GRANTEE_TYPE.usr;
    } else if (grantee.type == ZaItem.DL) {
        this.grantee.type = ZaGrant.GRANTEE_TYPE.grp;        
    } else {
        throw new AjxException("Invalid Grantee Type");
        return ;
    }
}

ZaEffectiveRights.prototype = new ZaItem ;
ZaEffectiveRights.prototype.constructor = ZaEffectiveRights ;

ZaEffectiveRights.A_grantee = "grantee" ;
ZaEffectiveRights.A_targets = "targets" ;
ZaEffectiveRights.A2_2currentTab = "secondCurrentTab" ;
ZaEffectiveRights.A2_3currentTab = "thirdCurrentTab" ;

ZaEffectiveRights.prototype.load =
function () {
    var soapDoc = AjxSoapDoc.create("GetAllEffectiveRightsRequest", ZaZimbraAdmin.URN, null);
    var elGrantee = soapDoc.set("grantee", this.grantee.id);
    elGrantee.setAttribute("type", this.grantee.type) ;
    elGrantee.setAttribute("by", "id") ;
    
    var params = {};
    params.soapDoc = soapDoc;
    params.asyncMode = false;
    var reqMgrParams = {
        controller : ZaApp.getInstance().getCurrentController(),
        busyMsg : com_zimbra_delegatedadmin.BUSY_GET_ALL_EFFECTIVE_RIGHTS
    }

    var resp = ZaRequestMgr.invoke(params, reqMgrParams);
    this.targets = resp.Body.GetAllEffectiveRightsResponse.target;
    this.grantee = resp.Body.GetAllEffectiveRightsResponse.grantee [0] ;

//    this.initTargetsFromJS (this.targets) ;
}

/*
ZaEffectiveRights.prototype.initTargetsFromJS = function (targets) {
    if (!targets) return ;
    var newTargets = {} ;
    for (var i = 0; i< targets.length; i ++) {
        newTargets[targets[i].type] = {} ;
        for (var k in targets[i]) {
            if (k == "all") {
                if (targets[i].all && targets[i].all.length == 1) {
                    for (var )
                    newTargets[targets[i].type].all = targets[i].all [0] ;
                }
            }
        }
    }
    
} */

ZaEffectiveRights.effectiveRightsItem = [
    {id: "getAttrs", ref: "getAttrs", type: _LIST_ },
    {id: "setAttrs", ref: "setAttrs", type: _LIST_},
    {id: "right", ref: "right" , type:_LIST_}
] ;

ZaEffectiveRights.er_target_item = [
    {id: "all", ref: "all", type: _OBJECT_, items: ZaEffectiveRights.effectiveRightsItem },
    {id: "inDomains", ref: "inDomains", type: _OBJECT_, items: ZaEffectiveRights.effectiveRightsItem },
    {id: "entries", ref: "all", type: _OBJECT_, items: ZaEffectiveRights.effectiveRightsItem }
]

ZaEffectiveRights.myXModel = {
    items:[
        {id: ZaEffectiveRights.A_grantee, ref: ZaEffectiveRights.A_grantee, type: _OBJECT_, items: [
                {id: "id", type: _STRING_, ref: "id" },
                {id: "name", type: _STRING_, ref: "name" },
                {id: "type", type: _STRING_, ref: "type" }
            ]
        },

        {id: ZaEffectiveRights.A_targets, ref: ZaEffectiveRights.A_targets, type: _OBJECT_ }
            
        /*
        {id: ZaEffectiveRights.A_targets, ref: ZaEffectiveRights.A_targets, type: _LIST_, listItem: [
                {id: "all", type: _LIST_, ref: "all", listItem: ZaEffectiveRights.effectiveRightsItem },
                {id: "inDomains", type: _LIST_, ref: "inDomains", listItem:[
                        {id: "domain", type: _LIST_, ref:"domain", listItem: [
                                { id: "name", ref: "name", type:_STRING_  }
                            ]
                        },
                        {id: "rights", type:_LIST_, ref: "rights", listItem: ZaEffectiveRights.effectiveRightsItem } 
                    ]
                },
                {id: "entries", ref: "entries", type: _LIST_, listItem:[
                        {id: "entry", type: _LIST_, ref:"entry", listItem: [
                                { id: "name", ref: "name", type:_STRING_  }
                            ]
                        },
                        {id: "rights", type:_LIST_, ref: "rights", listItem: ZaEffectiveRights.effectiveRightsItem }
                    ]
                },
                {id: "type", type: _STRING_, ref: "type" }
            ]
        } */
    ]
};




