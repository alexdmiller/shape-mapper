/*
	 Portions of this code are derived from the PeasyCam Processing library,
	 which is Copyright 2008 Jonathan Feinberg and licensed under the Apache
	 License, Version 2.0. The original license message appears below.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package spacefiller.peasy;

import spacefiller.peasy.PeasyCam.AbstractInterp;

class InterpolationManager {
	private AbstractInterp currentInterpolator = null;

	protected synchronized void startInterpolation(final AbstractInterp interpolation) {
		cancelInterpolation();
		currentInterpolator = interpolation;
		currentInterpolator.start();
	}

	protected synchronized void cancelInterpolation() {
		if (currentInterpolator != null) {
			currentInterpolator.cancel();
			currentInterpolator = null;
		}
	}

}