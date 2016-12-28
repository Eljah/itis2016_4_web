package ru.itis2016;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CalculatorServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        HttpSession session = request.getSession();

        session.setAttribute("digit", null);
        session.setAttribute("action", null);
        request.setAttribute("digit", null);
        request.setAttribute("mathaction", null);
        getServletContext().getRequestDispatcher("/calculator.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        String digit = request.getParameter("digit");
        String action = request.getParameter("mathaction");
        HttpSession ses = request.getSession();
        String ssDigit = (String) ses.getAttribute("digit");
        String ssAction = (String) ses.getAttribute("action");
        boolean numberError = false;
        if(digit.equals("")) {
            ses.setAttribute("digit", null);
            ses.setAttribute("action", null);
            ssDigit = "";
            ssAction = "";
        } else {
            try {
                if(digit.contains(",")) {
                    digit = digit.replace(",",".");
                }
                Double check = Double.valueOf(digit);
            } catch (NumberFormatException nfexc) {
                numberError = true;
                ssDigit = "<span style=\"color: red\">Only numbers available</span>";
                ses.setAttribute("digit", null);
                ses.setAttribute("action", null);
            }
            if(ssDigit == null && ssAction == null && !numberError) {
                ses.setAttribute("digit", digit);
                ses.setAttribute("action", action);
                ssDigit = (String) ses.getAttribute("digit");
                ssAction = (String) ses.getAttribute("action");
            } else if(!numberError) {
                String digitTemp = null;
                boolean error = false;
                if(ssAction.equals("+")) {
                    digitTemp = String.valueOf(Double.valueOf(ssDigit) + Double.valueOf(digit));
                } else if(ssAction.equals("-")) {
                    digitTemp = String.valueOf(Double.valueOf(ssDigit) - Double.valueOf(digit));
                } else if(ssAction.equals("*")) {
                    digitTemp = String.valueOf(Double.valueOf(ssDigit) * Double.valueOf(digit));
                } else if(ssAction.equals("/")) {
                    try {
                        digitTemp = String.valueOf(Double.valueOf(ssDigit) / Double.valueOf(digit));
                        if (Double.valueOf(digitTemp) == Double.POSITIVE_INFINITY ||
                                Double.valueOf(digitTemp) == Double.NEGATIVE_INFINITY)
                            throw new ArithmeticException();
                    } catch (ArithmeticException exc) {
                        digitTemp = "<span style=\"color:red\">Unavailable mathaction</span>";
                        error = true;
                        ses.setAttribute("digit", null);
                        ses.setAttribute("action", null);
                    }
                } else if(ssAction.equals("=")) {
                    digitTemp = ssDigit;
                }
                ses.setAttribute("digit", digitTemp);
                ses.setAttribute("action", action);
                ssDigit = (String) ses.getAttribute("digit");
                ssAction = (String) ses.getAttribute("action");
                if(ssAction.equals("=") || error) {
                    ses.setAttribute("digit", null);
                    ses.setAttribute("action", null);
                }
            }
        }

        CalculatorHistory.setSavedMathaction(ssAction);
        request.setAttribute("digit", ssDigit);
        request.setAttribute("mathaction", ssAction);
        getServletContext().getRequestDispatcher("/calculator.jsp").forward(request, response);
    }

}
