class LoadingAnimation {
	container = null;
	interval = null;

	constructor(parentContainer) {
		this.container = document.createElement('div');
		this.container.classList.add('toti-animation-input-loading-container');
		parentContainer.appendChild(this.container);
		parentContainer.style.position = 'relative';
		this.start();
	}
	start() {
		var dots = [];
		for (var i = 0; i < 3; i++) {
			var dot = document.createElement('div');
			dot.classList.add('toti-animation-input-loading-dot');
			dots.push(dot);
			this.container.appendChild(dot);
		}
		var index = 0;
		this.interval = setInterval(()=>{
			dots.forEach((dot)=>{
				dot.classList.remove('toti-animation-input-loading-dot-active');
			});
			if (index > 2) {
				index = 0;
			}
			dots[index].classList.add('toti-animation-input-loading-dot-active');
			index++;
		}, 400);
	}
	stop() {
		if (this.interval !== null) {
			clearInterval(this.interval);
		}
	}
	failure() {
		this.stop();
		this.container.innerHTML = '';
		var failure = document.createElement('div');
		failure.innerText = '!';
		failure.classList.add('toti-animation-input-loading-failure');
		this.container.appendChild(failure);
	}
	remove() {
		this.stop();
		this.container.remove();
	}
}

function animations(configuration) {
	var animations = {};
	function create(name, func) {
		if (configuration.hasOwnProperty(name)) {
			animations[name] = configuration[name];
		} else {
			animations[name] = func;
		}
	}
	create('inputLoading', (parentContainer)=>{
		return new LoadingAnimation(parentContainer);
	});

	return animations;
}