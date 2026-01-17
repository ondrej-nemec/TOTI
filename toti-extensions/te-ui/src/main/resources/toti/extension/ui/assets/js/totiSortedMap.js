class SortedMap {

	constructor() {
		this.order = [];
		this.data = {};
	}

	/**
	* Add value (or rewrite if exits) on given key
	* @param key mixed
	* @param value mixed
	* @return self
	*/
	put(key, value) {
		if (!this.exists(key)) {
			this.order.push(key);
		}
		this.data[key] = value;
		return this;
	}

	/**
	* Remove value by key
	* @param key mixed
	* @return value on given key or null if not exists
	*/
	get(key) {
		if (this.exists(key)) {
			return this.data[key];
		}
		return null;
	}

	/**
	* Remove value on given key
	* @param key mixed
	* @return removed value or null if key not exists
	*/
	remove(key) {
		var toDelete = this.get(key);
		var index = this.order.indexOf(key);
		if (index !== -1) {
			this.order.splice(index, 1);
		}
		delete this.data[key];
		return toDelete;
	}

	/**
	* Check if key exists in map
	* @param key mixed
	* @return boolean
	*/
	exists(key) {
		return this.data.hasOwnProperty(key);
	}

	/**
	* Iterate over all values using given function
	* @param callback function - arguments(in order): key, value, index
	*/
	forEach(callback) {
		var map = this;
		this.order.forEach(function(key, index) {
			callback(key, map.get(key), index);
		});
	}

	serialize() {
		var result = [];
		this.forEach(function(key, value, index) {
			var row = {};
			row[key] = value;
			result.push(row);
		});
		return JSON.stringify(result);
	}

	static deserialize(string) {
		var result = new SortedMap();
		JSON.parse(string).forEach(function(row) {
			var key = Object.keys(row)[0];
			result.put(key, row[key]);
		});
		return result;
	}

}