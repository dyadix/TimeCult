/*
 * Copyright (c) Rustam Vishnyakov, 2005-2009 (dyadix@gmail.com)
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * $Id: WorkspaceTest.java,v 1.1 2009/12/06 09:19:25 dyadix Exp $ 
 */
package net.sf.timecult.test.core;

import net.sf.timecult.model.ContainerFactory;
import net.sf.timecult.model.IdGenerator;
import net.sf.timecult.model.Project;
import net.sf.timecult.model.ProjectTreeItem;
import net.sf.timecult.model.Workspace;
import net.sf.timecult.model.Project.SortCriteria;
import net.sf.timecult.model.mem.LocalIdGenerator;
import net.sf.timecult.model.mem.MemContainerFactory;
import net.sf.timecult.model.mem.MemTimeLog;
import junit.framework.TestCase;

public class WorkspaceTest extends TestCase {
    
    private Workspace ws;

    protected void setUp() throws Exception {
        ContainerFactory cf = new MemContainerFactory();
        ws = new Workspace(cf);
        ws.setTimeLog(new MemTimeLog());
        ws.setIdGenerator(new LocalIdGenerator());
        //ws.addListener(this);            
        //_workspace.addIdleListener(new IdleStopwatchListener());
        ws.setSelection(ws);
    }


    protected void tearDown() throws Exception {
    }


    public void testGetItemType() {
        assertEquals(ProjectTreeItem.ItemType.WORKSPACE, ws.getItemType());
    }


    public void testIsDeletable() {        
        assertEquals(false, ws.isDeletable());
    }
    
    public void testGenerator() {
        IdGenerator idGen = ws.getIdGenerator();
        String firstId = idGen.getNewId();
        assertEquals("1", firstId);
        String secondId = idGen.getNewId();
        assertEquals("2", secondId);
        String lastId = idGen.getLastId();
        assertEquals("2", lastId);
    }
    
    
    public void testCreate() {
        final String proj1name = "Project1";
        final String proj1id = "001";
        final String proj2name = "Project2";
        Project project1 = ws.createChildProject(ws, proj1id, proj1name);
        assertNotNull(project1);
        assertEquals(proj1name, project1.getName());
        assertEquals(proj1id, project1.getId());
        Project project2 = ws.createChildProject(project1, null, proj2name);
        assertNotNull(project2);
        Project proj1subprojects[] = project1.getSubprojects(SortCriteria.DEFAULT);
        assertNotNull(proj1subprojects);
        assertEquals(1, proj1subprojects.length);
        assertEquals(project2, proj1subprojects[0]);
    }

}
