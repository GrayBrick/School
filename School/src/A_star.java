import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class A_star
{
    public static ArrayList<Part>   open_Parts = new ArrayList<>();

    public static boolean start_find_way(Part from, Part to)
    {
        for (ArrayList<Part> parts : algoritm.field)
        {
            for (Part part : parts)
            {
                part.open = false;
                part.close = false;
                part.dir = null;
                part.weight = 0;
            }
        }
        open_Parts.clear();
        return find_way(from, to);
    }

    public static boolean find_way(Part from, Part to)
    {
        if (from.equals(to))
            return true;

        for (Part part : from.getNear())
        {
            if (part.close || part.block || part.move)
                continue ;
            if (part.dir == null)
                part.dir = from;
            if (part.weight == 0)
                part.weight = part.distance(to);
            else if (part.weight > part.distance(to))
            {
                part.weight = part.distance(to);
                part.dir = from;
            }
            if (!part.open)
            {
                part.open = true;
                open_Parts.add(part);
            }
        }
        if (from.open)
        {
            open_Parts.remove(from);
            from.open = false;
        }
        from.close = true;
        if (open_Parts.size() == 0)
            return false;
        int index = -1;
        for (int i = 0; i < open_Parts.size(); i++)
        {
            if (index == -1)
                index = 0;
            else
                if (open_Parts.get(i).weight < open_Parts.get(index).weight)
                    index = i;
        }
        if (algoritm.type_output == 2)
            algoritm.print_field();

        return find_way(open_Parts.get(index), to);
    }

    public static ArrayList<Integer> getWay(Part from, Part to)
    {
        ArrayList<Integer> way = new ArrayList<>();

        while (!to.equals(from))
        {
            way.add(to.value);
            to = to.dir;
        }
        Collections.reverse(way);
        return  way;
    }

    public static ArrayList<int[]> getWayPosition(Part from, Part to)
    {
        ArrayList<int[]> way = new ArrayList<>();

        while (!to.equals(from))
        {
            way.add(to.position);
            to = to.dir;
        }
        Collections.reverse(way);
        return  way;
    }

    public static void do_way(ArrayList<Integer> way)
    {
        for (int part_value : way)
        {
            Part part = Part.get_part(part_value);
            part.move();
        }
    }
}
