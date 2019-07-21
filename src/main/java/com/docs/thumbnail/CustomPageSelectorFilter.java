package com.docs.thumbnail;

import org.jodconverter.filter.Filter;
import org.jodconverter.filter.FilterChain;
import org.jodconverter.office.OfficeContext;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.utils.Lo;
import org.jodconverter.office.utils.Write;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.datatransfer.XTransferable;
import com.sun.star.datatransfer.XTransferableSupplier;
import com.sun.star.frame.XController;
import com.sun.star.lang.XComponent;
import com.sun.star.text.XPageCursor;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.view.XSelectionSupplier;

public class CustomPageSelectorFilter implements Filter { 
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomPageSelectorFilter.class);

	  // This class has been inspired by these examples:
	  // https://wiki.openoffice.org/wiki/API/Tutorials/PDF_export
	  // https://blog.oio.de/2010/10/27/copy-and-paste-without-clipboard-using-openoffice-org-api

	  private final int page;

	  /**
	   * Creates a new filter that will select the specified page while converting a document (only the
	   * given page will be converted).
	   *
	   * @param page The page number to convert.
	   */
	  public CustomPageSelectorFilter(final int page) {
	    super();

	    this.page = page;
	  }

	  @Override
	  public void doFilter(
	      final OfficeContext context, final XComponent document, final FilterChain chain) {

	    LOGGER.debug("Applying the PageSelectorFilter");

	    // This filter can only be used with text document
	    if (Write.isText(document)) {
	      try {
			selectPage(document);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	    }

	    // Invoke the next filter in the chain
	    try {
			chain.doFilter(context, document);
		} catch (OfficeException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
		}
	  }

	  private void selectPage(final XComponent document) throws Exception {

	    // Querying for the interface XTextDocument (text interface) on the XComponent.
	    final XTextDocument docText = Write.getTextDoc(document);

	    // We need both the text cursor and the view cursor in order
	    // to select the whole content of the desired page.
	    final XController controller = docText.getCurrentController();
	    final XTextCursor textCursor = docText.getText().createTextCursor();
	    final XTextViewCursor viewCursor =
	        Lo.qi(XTextViewCursorSupplier.class, controller).getViewCursor();

	    // Reset both cursors to the beginning of the document
	    textCursor.gotoStart(false);
	    viewCursor.gotoStart(false);

	    // Querying for the interface XPageCursor on the view cursor.
	    final XPageCursor pageCursor = Lo.qi(XPageCursor.class, viewCursor);

	    // Jump to the page to select (first page is 1) and move the
	    // text cursor to the beginning of this page.
	    pageCursor.jumpToPage((short) page);
	    textCursor.gotoRange(viewCursor.getStart(), false);

	    // Jump to the end of the page and expand the text cursor
	    // to the end of this page.
	    pageCursor.jumpToEndOfPage();
	    textCursor.gotoRange(viewCursor.getStart(), true);

	    // Select the whole page.
	    final XSelectionSupplier selectionSupplier = Lo.qi(XSelectionSupplier.class, controller);
	    selectionSupplier.select(textCursor);

	    // Copy the selection (whole page).
	    final XTransferableSupplier transferableSupplier =
	        Lo.qi(XTransferableSupplier.class, controller);
	    final XTransferable xTransferable = transferableSupplier.getTransferable();

	    // Now select the whole document.
	    textCursor.gotoStart(false); // Go to the start
	    textCursor.gotoEnd(true); // Go to the end, expanding the cursor's text range
	    selectionSupplier.select(textCursor);

	    // Paste the previously copied page. This will replace the
	    // current selection (the whole document).
	    transferableSupplier.insertTransferable(xTransferable);
	  }

}

