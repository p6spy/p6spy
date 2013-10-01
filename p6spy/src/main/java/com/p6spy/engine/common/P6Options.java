/*
Copyright 2013 P6Spy

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
package com.p6spy.engine.common;

public abstract class P6Options {

    public P6Options() { }

    /* to implement options for a module:
     *  (1) extend this class
     *  (2) add static get and set methods for the options you want to make available
     *  (3) optionally override reload (not recommended)
     *  (4) return an instance of your options class in your factory via the getOptions() function
     */
    public void reload(P6SpyProperties properties) {
        P6LogQuery.debug(this.getClass().getName()+" reloading properties");
        properties.setClassValues(this.getClass());
    }

}
