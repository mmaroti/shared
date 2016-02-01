package mmaroti.ua.applet;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import java.io.StringWriter;
import java.applet.Applet;
import java.util.StringTokenizer;

import mmaroti.ua.symbol.*;
import mmaroti.ua.partial.ModelPrinter;
import mmaroti.ua.io.UaWriter;

public class ModelPrinterApplet extends Applet
{
	private static final long serialVersionUID = 1L;

	private Expression formula;

	private ModelPrinter printer;
	private StringWriter writer;

	private void createPrinter(String sizeString, String expString)
	{
		int size = Integer.parseInt(sizeString);

		String filter = "";
		
		StringTokenizer tokenizer = 
			new StringTokenizer(expString, "\n\r.");
			
		StringBuffer exp = new StringBuffer();
		while( tokenizer.hasMoreTokens() )
		{
			String token = tokenizer.nextToken().trim();
			if (token.length() == 0)
				continue;
			
			if( token.startsWith("only ") )
			{
				filter += " " + token.substring(5) + " ";
				continue;
			}
			
			if( exp.length() > 0 )
				exp.append("&");

			exp.append("(");
			exp.append(token);
			exp.append(")");
		}

		Parser parser = new Parser(FirstOrder.Operators);
		formula = parser.parse(exp.toString());

		writer = new StringWriter();

		printer = new ModelPrinter(formula, size, new UaWriter(writer), filter);
	}

	public String printAllModels(String sizeString, String expString)
	{
		try
		{
			createPrinter(sizeString, expString);
			printer.printAllModels();
			return writer.toString();
		}
		catch(Throwable e)
		{
			return e.toString();
		}
	}

	public static void main(String args[])
	{
		if( args.length <= 1 )
		{
			System.out.println(
				"Usage: java ModelPrinterApplet <size> <formula>");
			return;
		}
		else if (args[1].equals("poset"))
			args[1] = "r(x,x). (r(x,y)&r(y,x))->x=y. (r(x,y)&r(y,z))->r(x,z).";
	
		ModelPrinterApplet applet = new ModelPrinterApplet();
		applet.createPrinter(args[0], args[1]);
		applet.printer.printAllModels();
		System.out.println(applet.writer.toString());
	}

	public String printFirstModel(String sizeString, String expString)
	{
		try
		{
			createPrinter(sizeString, expString);
			printer.printFirstModel();
			return writer.toString();
		}
		catch(Throwable e)
		{
			return e.toString();
		}
	}
}
