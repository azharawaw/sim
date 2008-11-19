﻿// AboutButton.as



class AboutButton {
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function AboutButton() {
		debug("AboutButton initializing\n");
		
		var offset : Number = 3;
		
		var aboutButton : MovieClip = _level0.createEmptyMovieClip("aboutButton", _level0.getNextHighestDepth());
		var aboutBackground : MovieClip = aboutButton.createEmptyMovieClip("aboutBackground", 0);
		//var aboutButton : MovieClip = _level0.createEmptyMovieClip("aboutButton", 0);
		_level0.aboutButton = aboutButton;
		var aboutTextField : TextField = aboutButton.createTextField("aboutTextField", aboutButton.getNextHighestDepth(), 0, 0, 100, 100);
		var fmt : TextFormat = new TextFormat();
		fmt.font = '_sans';
		aboutTextField.text = "about";
		aboutTextField.setTextFormat(fmt);
		aboutBackground.lineStyle(0, 0x000000);
		aboutBackground.moveTo(0, 0);
		aboutBackground.beginFill(0xFFFFFF, 85);
		
		var hi : Number = aboutTextField.textHeight + 5 + 2 * offset;
		var wi : Number = aboutTextField.textWidth + 5 + offset;
		var voff : Number = offset + 8;
		
		aboutBackground.lineTo(0, hi);
		aboutBackground.lineTo(wi - hi + voff, hi);
		aboutBackground.curveTo(wi + voff, hi, wi + voff, 0);
		
		aboutBackground.endFill();
		aboutButton.useHandCursor = true;
		aboutButton.hitArea = aboutBackground;
		aboutButton.onRelease = function() : Void {
			_level0.debug("About Button clicked\n");
			//this._visible = false;
			new AboutDialog();
		};
		aboutButton._x = 0;
		aboutButton._y = 0;
		aboutTextField._x = offset;
		aboutTextField._y = offset;
	}
}
