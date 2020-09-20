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
        switch (algoritm.type_algo % 3)
        {
            case 0:
            {
                try
                {
                    return find_way(from, to);
                } catch(Exception ex)
                {
                    algoritm.type_algo++;
                    return  start_find_way(from, to);
                }
            }
            case 1:
            {
                try
                {
                    return find_way_wave(from, to, from.value);
                } catch(Exception ex)
                {
                    algoritm.type_algo++;
                    return  start_find_way(from, to);
                }
            }
            case 2:
            {
                try
                {
                    return find_way_greedy(from, to);
                } catch(Exception ex)
                {
                    algoritm.type_algo++;
                    return  start_find_way(from, to);
                }
            }
        }
        return false;
    }

    public static boolean find_way(Part from, Part to) throws Exception
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

    public static boolean   find_way_wave(Part from, Part to, int value) throws Exception
    {
        if (from.equals(to))
            return true;

        for (Part part : from.getNear())
        {
            if (part.close || part.block || part.move)
                continue ;
            if (part.dir == null)
                part.dir = from;
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
        if (algoritm.type_output == 2)
            algoritm.print_field();

        return find_way_wave(open_Parts.get(0), to, value);
    }

    public static boolean find_way_greedy(Part from, Part to) throws Exception
    {
        if (from.equals(to))
            return true;

        ArrayList<Part> open_Parts = new ArrayList<>();

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
            if (!open_Parts.contains(part))
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
        open_Parts.sort(Comparator.comparingInt(a -> a.weight));
        if (algoritm.type_output == 2)
            algoritm.print_field();

        for (Part part : open_Parts)
        {
            if (find_way_greedy(part, to))
                return true;
        }
        return false;
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
