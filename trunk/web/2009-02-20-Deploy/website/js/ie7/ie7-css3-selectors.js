/*
	IE7, version 0.9 (alpha) (2005-08-19)
	Copyright: 2004-2005, Dean Edwards (http://dean.edwards.name/)
	License: http://creativecommons.org/licenses/LGPL/2.1/
*/
IE7.addModule("ie7-css3-selectors",function(){cssQuery.addModule("css-level3",function(){selectors["~"]=function(r,f,t,n){var e,i;for(i=0;(e=f[i]);i++){while(e=nextElementSibling(e)){if(compareTagName(e,t,n))r.push(e)}}};pseudoClasses["contains"]=function(e,t){t=new RegExp(regEscape(getText(t)));return t.test(getTextContent(e))};pseudoClasses["root"]=function(e){return e==getDocument(e).documentElement};pseudoClasses["empty"]=function(e){var n,i;for(i=0;(n=e.childNodes[i]);i++){if(thisElement(n)||n.nodeType==3)return false}return true};pseudoClasses["last-child"]=function(e){return!nextElementSibling(e)};pseudoClasses["only-child"]=function(e){e=e.parentNode;return firstElementChild(e)==lastElementChild(e)};pseudoClasses["not"]=function(e,s){var n=cssQuery(s,getDocument(e));for(var i=0;i<n.length;i++){if(n[i]==e)return false}return true};pseudoClasses["nth-child"]=function(e,a){return nthChild(e,a,previousElementSibling)};pseudoClasses["nth-last-child"]=function(e,a){return nthChild(e,a,nextElementSibling)};pseudoClasses["target"]=function(e){return e.id==location.hash.slice(1)};pseudoClasses["checked"]=function(e){return e.checked};pseudoClasses["enabled"]=function(e){return e.disabled===false};pseudoClasses["disabled"]=function(e){return e.disabled};pseudoClasses["indeterminate"]=function(e){return e.indeterminate};AttributeSelector.tests["^="]=function(a,v){return"/^"+regEscape(v)+"/.test("+a+")"};AttributeSelector.tests["$="]=function(a,v){return"/"+regEscape(v)+"$/.test("+a+")"};AttributeSelector.tests["*="]=function(a,v){return"/"+regEscape(v)+"/.test("+a+")"};function nthChild(e,a,t){switch(a){case"n":return true;case"even":a="2n";break;case"odd":a="2n+1"}var ch=childElements(e.parentNode);function _5(i){var i=(t==nextElementSibling)?ch.length-i:i-1;return ch[i]==e};if(!isNaN(a))return _5(a);a=a.split("n");var m=parseInt(a[0]);var s=parseInt(a[1]);if((isNaN(m)||m==1)&&s==0)return true;if(m==0&&!isNaN(s))return _5(s);if(isNaN(s))s=0;var c=1;while(e=t(e))c++;if(isNaN(m)||m==1)return(t==nextElementSibling)?(c<=s):(s>=c);return(c%m)==s}});var firstElementChild=cssQuery.valueOf("firstElementChild");ie7CSS.pseudoClasses["root"]=function(e){return(e==viewport)||(!isHTML&&e==firstElementChild(body))};var _4=new ie7CSS.DynamicPseudoClass("checked",function(e){if(typeof e.checked!="boolean")return;var i=arguments;ie7CSS.addEventHandler(e,"onpropertychange",function(){if(event.propertyName=="checked"){if(e.checked)_4.register(i);else _4.unregister(i)}});if(e.checked)_4.register(i)});var _3=new ie7CSS.DynamicPseudoClass("enabled",function(e){if(typeof e.disabled!="boolean")return;var i=arguments;ie7CSS.addEventHandler(e,"onpropertychange",function(){if(event.propertyName=="disabled"){if(!e.isDisabled)_3.register(i);else _3.unregister(i)}});if(!e.isDisabled)_3.register(i)});var _2=new ie7CSS.DynamicPseudoClass("disabled",function(e){if(typeof e.disabled!="boolean")return;var i=arguments;ie7CSS.addEventHandler(e,"onpropertychange",function(){if(event.propertyName=="disabled"){if(e.isDisabled)_2.register(i);else _2.unregister(i)}});if(e.isDisabled)_2.register(i)});var _1=new ie7CSS.DynamicPseudoClass("indeterminate",function(e){if(typeof e.indeterminate!="boolean")return;var i=arguments;ie7CSS.addEventHandler(e,"onpropertychange",function(){if(event.propertyName=="indeterminate"){if(e.indeterminate)_1.register(i);else _1.unregister(i)}});ie7CSS.addEventHandler(e,"onclick",function(){_1.unregister(i)})});var _0=new ie7CSS.DynamicPseudoClass("target",function(e){var i=arguments;if(!e.tabIndex)e.tabIndex=0;ie7CSS.addEventHandler(document,"onpropertychange",function(){if(event.propertyName=="activeElement"){if(e.id==location.hash.slice(1))_0.register(i);else _0.unregister(i)}});if(e.id==location.hash.slice(1))_0.register(i)});decoder.add(/\|/,"\\:")});
