# Breakable List

Breakable list for Android allows you to create LinearLayout-like layout that breaks line when new View goes outside its field of view.
See app module for usage example.

![alt text](https://github.com/nikich59/breakable_list/blob/master/Screenshot_1555538865.png)

# Include

## Gradle
Add it in your root build.gradle at the end of repositories:

<code>
  allprojects {<br/>
		repositories {<br/>
			...<br/>
			maven { url 'https://jitpack.io' }<br/>
		}<br/>
	}
  </code>

Add the dependency:

<code>
  dependencies {<br/>
	        implementation 'com.github.nikich59:breakable_list:-SNAPSHOT'<br/>
	}
</code>

