package main.game.battlefield;

import main.system.auxiliary.GuiManager;
import main.system.auxiliary.StringMaster;
import main.system.math.PositionMaster;

import java.util.LinkedList;
import java.util.List;

public class Coordinates {

    private static int h;
    private static int w;
    private static boolean flipX;
    private static boolean flipY;
    private static boolean rotate;
    public int x;
    public int y;
    protected int z = 0;
    private boolean invalid = false;

    public Coordinates() {
        this.x = 0;
        this.y = x;
    }

    public Coordinates(int x, int y) {
        this(false, x, y);
    }

    public Coordinates(boolean custom, int x, int y) {
        if (!custom) {
            if (x >= GuiManager.getCurrentLevelCellsX()) {
                x = GuiManager.getCurrentLevelCellsX() - 1;
                this.setInvalid(true);
            }
            if (x < 0) {
                x = 0;
                this.setInvalid(true);
            }
            if (y >= GuiManager.getCurrentLevelCellsY()) {
                y = GuiManager.getCurrentLevelCellsY() - 1;
                this.setInvalid(true);
            }
            if (y < 0) {
                y = 0;
                this.setInvalid(true);
            }
        }
        this.x = x;
        this.y = y;
        if (flipX) {
            x = GuiManager.getCurrentLevelCellsX() - x;
        }
        if (flipY) {
            y = GuiManager.getCurrentLevelCellsY() - y;
        }
        if (rotate) {
            int buffer = x;
            x = y;
            y = buffer;
        }
    }

    public Coordinates(String s) {
        this(false, s);
    }

    public Coordinates(double x, double y) {
        this((int) x, (int) y);
    }

    public Coordinates(boolean custom, String s) {
        this(custom, StringMaster.getInteger(splitCoordinateString(s)[0].trim()), StringMaster
                .getInteger(splitCoordinateString(s)[1].trim()));
    }

    public Coordinates(Coordinates coordinates) {
        this(coordinates.getX(), coordinates.getY());
    }

    public static boolean withinBounds(int x, int y) {
        if (x < 0) {
            return false;
        }
        if (x >= GuiManager.getCurrentLevelCellsX()) {
            return false;
        }
        if (y < 0) {
            return false;
        }
        return y < GuiManager.getCurrentLevelCellsY();

    }

    public static String[] splitCoordinateString(String s) {
        return StringMaster.cropParenthesises(s).split(StringMaster.getCoordinatesSeparator());
    }

    public static Coordinates[] getCoordinates(String string) {
        if (string.isEmpty())
            return new Coordinates[0];
        Coordinates[] coordinates = new Coordinates[string.split(",").length];
        int i = 0;
        for (String s : string.split(",")) {
            Coordinates c = new Coordinates(s);
            coordinates[i] = c;
            i++;
        }
        return coordinates;
    }

    public static Coordinates getMiddleCoordinate(FACING_DIRECTION side) {
        return PositionMaster.getMiddleCoordinate(side);
    }

    public static void setFlipX(boolean flippedX) {
        flipX = flippedX;
    }

    public static void setFlipY(boolean flippedY) {
        flipY = flippedY;
    }

    public static void setRotated(boolean rotated) {
        rotate = rotated;

    }

    public int hashCode() {

        return x * 10 + y;
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof ZCoordinates) {
            if (this instanceof ZCoordinates) {
                ZCoordinates z1 = (ZCoordinates) this;
                ZCoordinates z2 = (ZCoordinates) arg0;
                if (z1.z != z2.z)
                    return false;
            }
        }
        if (arg0 instanceof Coordinates) {
            Coordinates c = (Coordinates) arg0;
            return c.x == x && c.y == y && c.z == z;
        }
        return false;
    }

    @Override
    public String toString() {
        return x + StringMaster.getCoordinatesSeparator() + y
                // + (z != 0 ? "; sublevel (Z): " + z : "")
                ;
    }

    public Coordinates invert() {
        if (h == 0)
            h = GuiManager.getBF_CompDisplayedCellsY();
        if (w == 0)
            w = GuiManager.getBF_CompDisplayedCellsX();
        this.x = w - 1 - x;
        this.y = h - 1 - y;
        main.system.auxiliary.LogMaster.log(2, "Inverted to " + toString());
        return this;
    }

    public Coordinates getAdjacentCoordinate(DIRECTION d, int i) {
        Coordinates c = null;
        for (; i > 0; i--) {
            c = getAdjacentCoordinate(d);
            if (c == null)
                return null;
        }
        return c;
    }

    public Coordinates getAdjacentCoordinate(DIRECTION direction) {
        return getAdjacentCoordinate(false, direction);
    }

    public Coordinates getAdjacentCoordinate(boolean allowInvalid, DIRECTION direction) {

        int x1 = x;
        int y1 = y;
        switch (direction) {
            case DOWN:
                y1++;
                break;
            case DOWN_LEFT:
                y1++;
                x1--;
                break;
            case DOWN_RIGHT:
                y1++;
                x1++;
                break;
            case LEFT:

                x1--;
                break;
            case RIGHT:
                x1++;
                break;
            case UP:
                y1--;
                break;
            case UP_LEFT:
                x1--;
                y1--;
                break;
            case UP_RIGHT:
                x1++;
                y1--;
                break;
            default:
                break;
        }
        if (!allowInvalid)
            if (!withinBounds(x1, y1))
                return null;
        return new Coordinates(allowInvalid, x1, y1);
    }

    // public boolean isAdjacent(Coordinates coordinates,
    // Boolean diagonals_included_not_only) {
    //
    // }
    public List<Coordinates> getAdjacentDiagonal() {
        return getAdjacent(true);
    }

    public List<Coordinates> getAdjacent(boolean diagonal) {
        List<Coordinates> list = new LinkedList<Coordinates>();
        Coordinates c = this;
        Coordinates e = null;
        if (!diagonal) {
            e = new Coordinates(c.x, c.y - 1);
            if (!e.isInvalid())
                list.add(e);

            e = new Coordinates(c.x - 1, c.y);
            if (!e.isInvalid())
                list.add(e);
            e = new Coordinates(c.x + 1, c.y);
            if (!e.isInvalid())
                list.add(e);

            e = new Coordinates(c.x, c.y + 1);
            if (!e.isInvalid())
                list.add(e);
        } else {
            e = new Coordinates(c.x - 1, c.y + 1);
            if (!e.isInvalid())
                list.add(e);
            e = new Coordinates(c.x + 1, c.y - 1);
            if (!e.isInvalid())
                list.add(e);

            e = new Coordinates(c.x - 1, c.y - 1);
            if (!e.isInvalid())
                list.add(e);

            e = new Coordinates(c.x + 1, c.y + 1);
            if (!e.isInvalid())
                list.add(e);
        }
        return list;
    }

    public List<Coordinates> getAdjacentOrthagonal() {
        return getAdjacent(false);
    }

    public List<Coordinates> getAdjacentCoordinates() {
        return getAdjacentCoordinates(true);
    }

    public List<Coordinates> getAdjacentCoordinates(Boolean diagonals_included_not_only) {
        List<Coordinates> list = new LinkedList<Coordinates>();

        if (diagonals_included_not_only != null) {
            if (diagonals_included_not_only)
                list.addAll(getAdjacentDiagonal());
            list.addAll(getAdjacentOrthagonal());
        } else {
            list.addAll(getAdjacentDiagonal());
        }

        return list;

    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public boolean isAdjacent(Coordinates coordinates, Boolean diagonals_included_not_only) {
        if (diagonals_included_not_only == null) {
            return Math.abs(coordinates.x - x) == 1 && Math.abs(coordinates.y - y) == 1;
        }
        if (diagonals_included_not_only) {
            if (Math.abs(coordinates.x - x) == 1)
                if (Math.abs(coordinates.y - y) <= 1)
                    return true;
            if (Math.abs(coordinates.y - y) == 1)
                if (Math.abs(coordinates.x - x) <= 1)
                    return true;
            return false;
        }
        if (Math.abs(coordinates.x - x) == 1)
            if (Math.abs(coordinates.y - y) < 1)
                return true;
        if (Math.abs(coordinates.y - y) == 1)
            if (Math.abs(coordinates.x - x) < 1)
                return true;
        return false;
        // [OPTIMIZED] return
        // getAdjacentCoordinates(diagonals_included_not_only).contains(coordinates);
    }

    public boolean isAdjacent(Coordinates coordinates) {
        if (Math.abs(coordinates.x - x) == 1)
            if (Math.abs(coordinates.y - y) <= 1)
                return true;
        if (Math.abs(coordinates.y - y) == 1)
            if (Math.abs(coordinates.x - x) <= 1)
                return true;
        return false;
        // [OPTIMIZED] return
        // getAdjacentCoordinates(true).contains(coordinates);
    }

    public Coordinates getOffsetByX(int i) {
        return new Coordinates(x + i, y);
    }

    public Coordinates getOffsetByY(int i) {
        return new Coordinates(x, y + i);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getXorY(boolean xOrY) {
        return xOrY ? x : y;
    }

    public enum FACING_DIRECTION {
        NORTH(DIRECTION.UP, true, true),
        WEST(DIRECTION.LEFT, false, true),
        EAST(DIRECTION.RIGHT, false, false),
        SOUTH(DIRECTION.DOWN, true, false),
        NONE(null, false, false);

        private DIRECTION direction;
        private boolean vertical;
        private boolean closerToZero;

        FACING_DIRECTION(DIRECTION direction, boolean vertical, boolean closerToZero) {
            this.setDirection(direction);
            this.setVertical(vertical);
            this.setCloserToZero(closerToZero);
        }

        public DIRECTION getDirection() {
            return direction;
        }

        public void setDirection(DIRECTION direction) {
            this.direction = direction;
        }

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }

        public boolean isVertical() {
            return vertical;
        }

        public void setVertical(boolean vertical) {
            this.vertical = vertical;
        }

        public boolean isCloserToZero() {
            return closerToZero;
        }

        public void setCloserToZero(boolean closerToZero) {
            this.closerToZero = closerToZero;
        }

        // public FACING_DIRECTION rotate180() {
        // return
        // FacingMaster.getFacingFromDirection(DirectionMaster.rotate180(getDirection()));
        // }
        //
        // public DIRECTION flip() {
        // return DirectionMaster.flip(this);
        // }
    }

    public enum UNIT_DIRECTION {
        AHEAD(0),
        AHEAD_LEFT(45),
        AHEAD_RIGHT(-45),
        LEFT(90),
        RIGHT(-90),
        BACKWARDS(180),
        BACKWARDS_LEFT(135),
        BACKWARDS_RIGHT(-135),;

        private int degrees;

        UNIT_DIRECTION(int degrees) {
            this.degrees = degrees;
        }

        public int getDegrees() {
            return degrees;
        }
    }

    public enum DIRECTION {
        UP(false, 90, true) {
            public Boolean isGrowY() {
                return false;
            }
        },
        DOWN(false, 270, true) {
            public Boolean isGrowY() {
                return true;
            }
        },
        LEFT(false, 180) {
            public Boolean isGrowX() {
                return false;
            }
        },
        RIGHT(false, 360) {
            public Boolean isGrowX() {
                return true;
            }
        },

        UP_LEFT(true, 135, true) {
            public Boolean isGrowX() {
                return false;
            }

            public Boolean isGrowY() {
                return false;
            }
        },
        UP_RIGHT(true, 45, true) {
            public Boolean isGrowX() {
                return true;
            }

            public Boolean isGrowY() {
                return false;
            }
        },
        DOWN_RIGHT(true, 225, true) {
            public Boolean isGrowX() {
                return true;
            }

            public Boolean isGrowY() {
                return true;
            }
        },
        DOWN_LEFT(true, 315, true) {
            public Boolean isGrowX() {
                return false;
            }

            public Boolean isGrowY() {
                return true;
            }
        },;
        private boolean vertical;

        private boolean diagonal;
        private int degrees;

        DIRECTION(boolean diagonal, int degrees, boolean vertical) {
            setDiagonal(diagonal);
            this.setDegrees(degrees);
            this.vertical = vertical;
        }

        DIRECTION(boolean diagonal, int degrees) {
            this(diagonal, degrees, false);
        }

        public DIRECTION getXDirection() {
            if (this == RIGHT || this == UP_RIGHT || this == DIRECTION.DOWN_RIGHT)
                return RIGHT;
            if (this == LEFT || this == DOWN_LEFT || this == DIRECTION.UP_LEFT)
                return LEFT;
            return null;
        }

        public DIRECTION getYDirection() {
            if (this == UP || this == UP_RIGHT || this == DIRECTION.UP_LEFT)
                return UP;
            if (this == DOWN || this == DOWN_RIGHT || this == DIRECTION.DOWN_LEFT)
                return DOWN;
            return null;
        }

        public boolean isDiagonal() {
            return diagonal;
        }

        public void setDiagonal(boolean diagonal) {
            this.diagonal = diagonal;
        }

        public int getDegrees() {
            return degrees;
        }

        public void setDegrees(int degrees) {
            this.degrees = degrees;
        }

        public Boolean isGrowX() {
            return null;
        }

        public Boolean isGrowY() {
            return null;
        }

        public boolean isVertical() {
            return vertical;
        }

        public DIRECTION rotate90(boolean clockwise) {
            return DirectionMaster.rotate90(this, clockwise);
        }

        public DIRECTION rotate45(boolean clockwise) {
            return DirectionMaster.rotate45(this, clockwise);
        }

        public DIRECTION rotate180() {
            return DirectionMaster.rotate180(this);
        }

        public DIRECTION flip() {
            return DirectionMaster.flip(this);
        }

    }

}
