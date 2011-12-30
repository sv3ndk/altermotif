var editAssetCtrl;

$(document).ready(function() {
	editAssetCtrl = new EditAssetController();
	editAssetCtrl.init();
})

//////////////////////////
// EditAssetController class
//////////////////////////


function EditAssetController() {

	/////////////
	// members
	this.koModel = new EditAssetViewModel();
	this.clickedAssetId;
	this.userListPopup;
	
	
	/////////////
	// public API
	
	this.init = function() {
		var self = this;
		
		// knockout bindings 
		ko.applyBindings(this.koModel, $("#editProjectAssetsContainer")[0]);
		
		// click on add asset 
		$("#addProjectAssetLink").click(function(){
			self.koModel.addEmptyAsset();
		});
		
		// click on remove asset
		new Confirm.AskAndProceed(this, "#editProjectAssetsContainer", "img.deleteTaskLink", confirmRemoveProjectAssetText, this.onClickOnDeleteAsset, this.afterUserConfirmsRemoveAsset).init();
		
		// click "add assignee"
		this.userListPopup = new UserListPopup(this, chooseAssetAssigneePopupTitle, this.afterUserSelectsAssignee);
		this.userListPopup.init();
		this.koModel.maxNumOfAssigneePerTask = this.userListPopup.countTotalNumberOfPopupUser(); 
		$("#editProjectAssetsContainer").on("click", ".addAssetAssigneeLink", function(event){
			self.clickedAssetId = $(event.target).parent().parent().find(".hiddenAssetId").text();
			var excludedUsernames = self.koModel.getAsset(self.clickedAssetId).getListOfAssigneesNames();
			self.userListPopup.openFiltered(excludedUsernames);
		});
	}
	
	
	/////////////
	// internal methods
	
	this.onClickOnDeleteAsset = function (self, event) {
		self.clickedAssetId = $(event.target).parent().parent().find(".hiddenAssetId").text();
	};
	
	this.afterUserConfirmsRemoveAsset = function(self, event) {
		self.koModel.removeAsset(self.clickedAssetId);
	};
	
	this.afterUserSelectsAssignee = function (self, username) {
		self.koModel.addAssignee(self.clickedAssetId, username);
	};
	
}



////////////////////////////////////
// EditAssetViewModel controller
// KO root ModelView instance for this page
////////////////////////////////////

function EditAssetViewModel() {
	
	/////////////////////////
	// members
	this.assets = ko.observableArray();
	this.lastCreatedAssetId = 0;
	this.maxNumOfAssigneePerTask;
	
	
	/////////////////////////
	// public API

	this.addEmptyAsset = function () {
		this.addStaticAsset(new StaticResource(this.getNextCreatedAssetIndex(), "", "todo", "", []), true);
	};
	
	this.addStaticAsset = function(staticAsset, isNew) {
		this.addAsset(new ViewProjectResource(staticAsset, isNew, this.maxNumOfAssigneePerTask, "default asset name").init());
	};
	
	this.addAsset = function (dynamicAsset) {
		this.assets.push(dynamicAsset);
		dabUtils.makeInputDatePicker("#editProjectAssetsContainer input.dueDateInput", '-0:+100');		
	};
	
	this.removeAsset = function (assetId) {
		var removedAsset;
		$(this.assets()).each(function(index, asset) {
			if (asset.id == assetId) {
				removedAsset = asset;
			}
		});
		this.assets.remove(removedAsset);
	};
	
	this.addAssignee = function (assetId, username) {
		$(this.assets()).each(function(index, asset) {
			if (asset.id == assetId) {
				asset.addAssignee(username);
			}
		});
	};
	
	this.getAsset = function (assetId) {
		var foundAsset;
		$(this.assets()).each(function(index, asset) {
			if (asset.id == assetId) {
				foundAsset = asset;
			}
		});
		return foundAsset;
	};
	
	///////////////////////////
	// internal methods
	
	this.getNextCreatedAssetIndex = function() {
		this.lastCreatedAssetId ++;
		return "new" + this.lastCreatedAssetId; 
	};

}