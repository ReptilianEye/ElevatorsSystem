package org.example.elevators.strategy;

import org.example.elevators.model.Direction;
import org.example.elevators.model.Elevator;
import org.example.elevators.model.Floor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ElevatorStrategy {
    private final int minFloor;
    private final int maxFloor;
    Map<Integer, Query> queries;

    public ElevatorStrategy(int minFloor, int maxFloor) {
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
    }

    public void run(List<Elevator> elevatorList, List<Floor> floorList) {
        List<Elevator> elevatorsEmpty = new LinkedList<>();
        List<Elevator> elevatorsContainingPeople = new LinkedList<>();
        elevatorList.forEach(elevator -> {
            if (elevator.isNotEmpty())
                elevatorsContainingPeople.add(elevator);
            else elevatorsEmpty.add(elevator);
        });
        queries = floorList.stream().map(floor -> new Query(floor.getButton().isPressed(Direction.UP), floor.getButton().isPressed(Direction.DOWN), floor.getFloorNumber())).collect(Collectors.toMap(Query::getFloor, Function.identity()));
        for (Elevator elevator : elevatorsContainingPeople) {
            int i = elevator.getCurrentFloor();
            while (true) {
                queries.get(i).untag(elevator.getDirection());
                i += elevator.getDirection().getValue();
                if (i == elevator.getTargetFloor())
                    queries.get(i).untagAll();
                break;
            }
        }

        for (Elevator elevator : elevatorsEmpty) {
            int currentFloor = elevator.getCurrentFloor();
            int closestFloor = getFarthestFloorInSameDirection(currentFloor);
            Direction direction;

            if (closestFloor == -1) {
                closestFloor = getFarthestRequest(currentFloor);
                if (closestFloor == -1) {
                    return; // ?
                }
                direction = closestFloor > currentFloor ? Direction.DOWN : Direction.UP;
            } else
                direction = closestFloor > currentFloor ? Direction.UP : Direction.DOWN;

            elevator.setTargetFloor(closestFloor);
            untagAllInDirection(closestFloor, currentFloor, direction); //elevator will go all the way to farthest request and then come back
        }
    }

    private int getFarthestFloorInSameDirection(int startingFloor) {
        int farthestFloorUp = -1;
        for (int i = maxFloor; i > startingFloor; i--) {
            if (queries.get(i).isUp()) {
                farthestFloorUp = i;
                break;
            }
        }
        int farthestFloorDown = -1;
        for (int i = minFloor; i < startingFloor; i++) {
            if (queries.get(i).isDown()) {
                farthestFloorDown = i;
                break;
            }
        }
        return calcFarthestFloor(startingFloor, farthestFloorUp, farthestFloorDown);
    }

    private void untagAllInDirection(int startingFloor, int targetFloor, Direction direction) {
        while (true) {
            queries.get(startingFloor).untag(direction);
            if (startingFloor == targetFloor)
                break;
            startingFloor += direction.getValue();
        }
    }

    private int getFarthestRequest(int startingFloor) {
        int farthestFloorUp = -1;
        for (int i = maxFloor; i >= startingFloor; i--) {
            if (queries.get(i).tagged()) {
                farthestFloorUp = i;
                break;
            }
        }
        int farthestFloorDown = -1;
        for (int i = minFloor; i <= startingFloor; i++) {
            if (queries.get(i).tagged()) {
                farthestFloorDown = i;
                break;
            }
        }
        return calcFarthestFloor(startingFloor, farthestFloorUp, farthestFloorDown);
    }

    private int calcFarthestFloor(int starting, int f1, int f2) {
        if (f1 == -1) return f2;
        if (f2 == -1) return f1;
        if (Math.abs(f1 - starting) > Math.abs(f2 - starting))
            return f1;
        return f2;
    }

}