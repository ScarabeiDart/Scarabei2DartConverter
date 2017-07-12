
package com.jfixby.scarabei.red.debug;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.debug.DEBUG_TIMER_MODE;
import com.jfixby.scarabei.api.debug.DebugComponent;
import com.jfixby.scarabei.api.debug.DebugTimer;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.api.sys.settings.ExecutionMode;
import com.jfixby.scarabei.api.sys.settings.SystemSettings;

public class RedDebug implements DebugComponent {

	
	public void printCallStack (final boolean condition) {
		if (!condition) {
			return;
		}
		printStack();
	}

	final static private void printStack () {
		final CallStack stack = new CallStack();
		final List<StackTraceElement> list = Collections.newList(stack.getStackTrace());
		list.reverse();
		list.removeLast();
		list.removeLast();
		list.removeLast();
		list.print("call stack");
	}

	
	public  T checkNull (final String parameter_name, final T value) {
		if (value == null) {
			Err.reportError("<" + parameter_name + "> is null.");
		}
		return value;
	}

	
	public  T checkNull (final T value) {
		if (value == null) {
			Err.reportError("Paremeter is null.");
		}
		return value;
	}

	
	public String checkEmpty (final String parameter_name, final String value) {
		if ("".equals(value)) {
			Err.reportError("<" + parameter_name + "> is empty.");
		}
		return value;
	}

	
	public void exit (final boolean condition) {
		if (condition) {
			Sys.exit();
		}
	}

	
	public void printCallStack () {
		printStack();
	}

	
	public void reportWarning (final String msg) {
		if (SystemSettings.executionModeCovers(ExecutionMode.EARLY_DEVELOPMENT)) {
			Err.reportError(msg);
		} else {
			L.e("WARNING", msg);
			this.printCallStack();
		}
	}

	
	public void checkTrue (final String flag_name, final boolean flag) {
		if (flag == false) {
			Err.reportError(flag_name + " is " + flag);
		}
	}

	
	public void checkTrue (final boolean flag) {
		if (flag == false) {
			Err.reportError("flag is " + flag);
		}
	}

	
	public DebugTimer newTimer () {
		return new RedDebugTimer(DEBUG_TIMER_MODE.NANOSECONDS);
	}

	
	public DebugTimer newTimer (final DEBUG_TIMER_MODE mode) {
		return new RedDebugTimer(mode);
	}

}
