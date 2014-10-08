package org.openpixi.pixi.physics.fields;

import org.openpixi.pixi.parallel.cellaccess.CellAction;
import org.openpixi.pixi.physics.grid.Cell;
import org.openpixi.pixi.physics.grid.Grid;

public class SimpleSolver extends FieldSolver {

	private Solve solve = new Solve();
        


	/**A simple LeapFrog algorithm
	 * @param grid before the update: E(t), B(t+dt/2);
	 * 						after the update: E(t+dt), B(t+3dt/2)
	*/
	@Override
	public void step(Grid grid, double timeStep) {
		this.solve.timeStep = timeStep;
		cellIterator.execute(grid, solve);
	}


	private class Solve implements CellAction {

	private double timeStep = 0.0;
		public void execute(Cell cell) {
			throw new UnsupportedOperationException();
		}

		public void execute(Grid grid, int x, int y) {
			/**curl of the E field using forward difference since the B field is located in the center
                         of the grid and the E-field is located at the edges*/
			double cz = (grid.getEyo(x+1, y) - grid.getEyo(x, y)) / (grid.getCellWidth()) -
					(grid.getExo(x, y+1) - grid.getExo(x, y)) / (grid.getCellHeight());

			/**Maxwell equations*/
			grid.addBz(x, y, -timeStep * cz);

			/**curl of the B field using center difference*/
			double cx = (grid.getBzo(x, y) - grid.getBzo(x, y-1)) / (grid.getCellHeight());
			double cy = -(grid.getBzo(x, y) - grid.getBzo(x-1, y)) / (grid.getCellWidth());
                        
			/**Maxwell EQ*/
			grid.addEx(x, y, timeStep * (cx - 4 * Math.PI * grid.getJx(x, y)));
			grid.addEy(x, y, timeStep * (cy - 4 * Math.PI * grid.getJy(x, y)));
		}
	}

}
