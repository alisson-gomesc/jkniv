package be.jkniv.whinstone.fluent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RegisterCommand
{
    private final List<Command> commands = Collections.synchronizedList(new LinkedList<Command>());

    public void add(Command command) {
        commands.add(command);
    }

    public void removeLast() {
        //TODO: add specific test for synchronization of this block (it is tested by InvocationContainerImplTest at the moment)
        synchronized (commands) {
            int last = commands.size() - 1;
            commands.remove(last);
        }
    }
/*
    public List<Command> getAll() {
        List<Command> copiedList;
        synchronized (commands) {
            copiedList = new LinkedList<Command>(commands) ;
        }

        return ListUtil.filter(copiedList, new RemoveToString());
    }
*/
    public boolean isEmpty() {
        return commands.isEmpty();
    }
/*
    private static class RemoveToString implements Filter<Invocation> {
        public boolean isOut(Invocation invocation) {
            return new ObjectMethodsGuru().isToString(invocation.getMethod());
        }
    }
*/
}
