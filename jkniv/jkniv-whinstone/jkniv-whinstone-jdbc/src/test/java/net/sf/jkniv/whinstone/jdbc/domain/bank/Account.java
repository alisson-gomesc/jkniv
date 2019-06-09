package net.sf.jkniv.whinstone.jdbc.domain.bank;

public class Account
{
    private String name;
    private int          balance = 0;
    
    public Account()
    {
        this("",0);
    }
    
    public Account(String name, int balance)
    {
        this.name = name;
        this.balance = balance;
    }
    
    public synchronized void withdraw(int amount)
    {
        if (amount > this.balance)
            throw new InsufficientFundsException("balance insufficient [" + balance + "] to withdraw " + amount);
        this.balance -= amount;
    }
    
    public synchronized void deposit(int amount)
    {
        this.balance += amount;
    }

    public synchronized void transfer(int amount, Account b)
    {
        withdraw(amount);
        b.deposit(amount);
    }

    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
 
    public int getBalance()
    {
        return this.balance;
    }
    
    public void setBalance(int balance)
    {
        this.balance = balance;
    }

    @Override
    public String toString()
    {
        return "Account [name=" + name + ", balance=" + balance + "]";
    }
}
