/* <module> - <description>
 * Copyright (C) 2026  Krab
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

/// This is an internal module providing facilities for creating and
/// outputting Java classes. It is mainly used by the annotation
/// processor, and the package is not exported. Documentation is
/// provided anyway, as a matter of maintainability.
/// 
/// Classes provided in this package all follow a similar pattern closely
/// related to the _builder_ design pattern. The idea is that building
/// a class is done by chaining methods one after another. Some methods
/// "interrupts" the flow by providing a "continuation", here, a lambda
/// that defines a builder to be inserted in the main tree. This allows
/// for very efficiently written bits of classes, methods, and so on,
/// compared to using a [StringBuilder] for instance, where things have
/// to be added one bit at a time.
/// 
/// @see org.krab.json.annotation.processor.JsonAnnotationProcessor
/// @author krab5
/// @version 1.0
package org.krab.json.annotation.processor.builder;

