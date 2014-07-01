package zombies;

import java.util.*;

import repast.simphony.query.space.grid.*;
import repast.simphony.random.*;
import repast.simphony.space.*;
import repast.simphony.space.continuous.*;
import repast.simphony.space.grid.*;
import repast.simphony.util.*;

public class Human {

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private int energy, startingEnergy;

	public Human(ContinuousSpace<Object> space, Grid<Object> grid, int energy) {
		this.space = space;
		this.grid = grid;
		this.energy = startingEnergy = energy;
	}

	@Watch ( watcheeClassName = "zombies.Zombie", watcheeFieldNames = "moved", query = " within_moore 1", whenToTrigger = WatcherTriggerSchedule . IMMEDIATE )
	public void run() {
		GridPoint pt = grid.getLocation(this);

		GridCellNgh<Zombie> nghCreator = new GridCellNgh<Zombie>(grid, pt,
				Zombie.class, 1, 1);
		List<GridCell<Zombie>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

		GridPoint pointWithLeastZombies = null;
		int minCount = Integer.MAX_VALUE;
		for (GridCell<Zombie> cell : gridCells) {
			if (cell.size() < minCount) {
				pointWithLeastZombies = cell.getPoint();
				minCount = cell.size();
			}
		}

		if (energy > 0) {
			moveTowards(pointWithLeastZombies);
		} else {
			energy = startingEnergy;
		}
	}

	private void moveTowards(GridPoint pt) {
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint,
					otherPoint);
			space.moveByVector(this, 2, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
			energy--;
		}
	}

}
