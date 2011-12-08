/*
 * Copyright (C) 2011 Alex Kuiper
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.nightwhistler.pageturner.sync;

public interface ProgressService {

	/**
	 * Stores the progress for the given book.
	 * @param userId
	 * @param fileName
	 * @param progress
	 */
	public void storeProgress( String fileName, int index, int progress );
	
	/**
	 * Returns the progress, or -1 of it wasn't found.
	 * 
	 * @param fileName
	 * @return
	 */
	public BookProgress getProgress( String fileName );
	
	public void setEmail(String email);
	
	
	
}
