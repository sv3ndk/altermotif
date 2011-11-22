package com.svend.dab.web.upload;

import javax.servlet.http.HttpServlet;

/**
 * 
 * Separate servlet, not related at all to JSF2 stack, where we receive the uploaded document and photos.
 * 
 * This just tries to read the parameters POSTed from the browser and builds an {@link UploadRequest} based on that, which is then analysed by the {@link IUploadProcessor}
 * 
 * @author Svend
 * 
 */
//@WebServlet(name = "UploadSrv", urlPatterns = "/upl")
//@MultipartConfig
public class UploadServlet3 extends HttpServlet {

//	private static final long serialVersionUID = 1571431148020967114L;
//
//
//
//	// -----------------------------------
//	// helper beans
//
//	private IUploadProcessor uploadProcessor; // = new AwsUploadProcessor();
//
//	/**
//	 * DI. This cannot be done in a @PostConstruct of the servlet: we have no servlet context at that point yet => lazy init 
//	 */
//	public void initServlet() {
//		if (uploadProcessor == null) {
//			synchronized (this) {
//				if (uploadProcessor == null) {
//					WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
//					uploadProcessor = (IUploadProcessor) context.getBean("AwsUploadProcessor");
//				}
//			}
//		}
//	}
//	
//	
//	private static Logger logger = Logger.getLogger(UploadServlet.class.getName());
//
//	// -----------------------------------
//	// config
//
//	// -----------------------------------
//	// -----------------------------------
//	// -----------------------------------
//
//	@Override
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//		initServlet();
//
//		UploadRequest uploadRequest = new UploadRequest();
//		try {
//			uploadRequest = parseUploadRequest(request);
//			validateUploadRequest(request, uploadRequest);
//			uploadProcessor.processUploadRequest(uploadRequest);
//			response.sendRedirect(request.getServletContext().getContextPath() + uploadRequest.getUploadType().sucessfullUploadNavigationOutcome);
//
//		} catch (UploadFailedException e) {
//
	
//			request.getSession().setAttribute(SESSION_ATTR_SUGGESTED_NAVIGATION, uploadRequest.getUploadType().failedUploadNavigationSuggestedOutcome);
//
//			if (e.getReason() == null) {
//				// this should never happen, defaulting to a generic error message
//				logger.log(Level.SEVERE, "Could not process uploaded request, but the exception for the failed upload does not contain a reason, this is weird...", e);
//				request.getSession().setAttribute(SESSION_ATTR_ERROR_MESSAGE_KEY, UploadFailedException.failureReason.technicalError.getErrorMessageKey());
//			} else {
//				logger.log(Level.SEVERE, "Could not process uploaded request, upload failure reason: " + e.getReason(), e);
//				request.getSession().setAttribute(SESSION_ATTR_ERROR_MESSAGE_KEY, e.getReason().getErrorMessageKey());
//			}
//
//			response.sendRedirect(request.getServletContext().getContextPath() + "/error");
//
//		} catch (Exception e) {
//			logger.log(Level.SEVERE, "could not process upload request: generic error", e);
//
//			request.getSession().setAttribute(SESSION_ATTR_SUGGESTED_NAVIGATION, uploadRequest.getUploadType().failedUploadNavigationSuggestedOutcome);
//			request.getSession().setAttribute(SESSION_ATTR_ERROR_MESSAGE_KEY, UploadFailedException.failureReason.technicalError.getErrorMessageKey());
//			response.sendRedirect(request.getServletContext().getContextPath() + "/error");
//		}
//	}
//
//	// ----------------------------------------
//	//
//	// ----------------------------------------
//
//	/**
//	 * @param request
//	 * @return
//	 */
//	private UploadRequest parseUploadRequest(HttpServletRequest request) {
//
//		final UploadRequest uploadRequest = new UploadRequest();
//		try {
//			try {
//				uploadRequest.setUploadType(UPLOAD_TYPE.valueOf(parseStringFailing(request, uploadtype)));
//			} catch (IllegalArgumentException e) {
//				// makes sure UploadType is never null (we need it in order to navigate => this is also the reason why we parse this one first)
//				uploadRequest.setUploadType(UPLOAD_TYPE.UNKNOWN);
//				throw new UploadFailedException("could not parse uploaded file", e);
//			}
//
//			uploadRequest.setUsername(parseStringFailing(request, username));
//			uploadRequest.setUploadPermKey(parseStringFailing(request, permkey));
//			uploadRequest.setStream(parseInputStreamFailing(request, theFile));
//
//		} catch (UploadFailedException e) {
//			throw e;
//		} catch (Exception e) {
//			logger.log(Level.SEVERE, "Could not parse upload POST request", e);
//			throw new UploadFailedException("could not parse uploaded file", e);
//		}
//
//		return uploadRequest;
//	}
//
//	/**
//	 * @param request
//	 */
//	private void validateUploadRequest(HttpServletRequest request, UploadRequest uploadRequest) {
//		if (request.getContentLength() > uploadRequest.getUploadType().maxUploadSize) {
//			throw new UploadFailedException("Max file size exceeded: " + request.getContentLength() + " > " + uploadRequest.getUploadType().maxUploadSize, fileTooBig);
//		}
//	}
//
//	/**
//	 * parses one named part of the {@link HttpServletRequest} as a {@link String}, or returns null if this part name is not found
//	 * 
//	 * @param request
//	 * @param partName
//	 * @return
//	 * @throws IOException
//	 * @throws ServletException
//	 */
//	private String parseString(HttpServletRequest request, String partName) throws IOException, ServletException {
//		Part part = request.getPart(partName);
//
//		if (part == null) {
//			return null;
//		}
//
//		Scanner scanner = new Scanner(part.getInputStream());
//		if (scanner.hasNext()) {
//			return scanner.next();
//		} else {
//			return null;
//		}
//
//	}
//
//	/**
//	 * Same as parseString above, but throws an {@link UploadFailedException} in case this part name is not found
//	 * 
//	 * @param request
//	 * @param partName
//	 * @return
//	 * @throws IOException
//	 * @throws ServletException
//	 */
//	private String parseStringFailing(HttpServletRequest request, FORM_ITEM_NAME partName) throws IOException, ServletException {
//		String partStringContent = parseString(request, partName.name());
//		if (partStringContent == null) {
//			throw new UploadFailedException("could not find part " + partName + " in upload request (or found a null value)", partNotFound);
//		} else {
//			return partStringContent;
//		}
//	}
//
//	/**
//	 * Retrieve the input stream with this name in the POST request, or throws an {@link UploadFailedException} if not found
//	 * 
//	 * @param request
//	 * @param partName
//	 * @return
//	 * @throws IOException
//	 * @throws ServletException
//	 */
//	private InputStream parseInputStreamFailing(HttpServletRequest request, FORM_ITEM_NAME partName) throws IOException, ServletException {
//		Part part = request.getPart(partName.name());
//
//		if (part == null) {
//			throw new UploadFailedException("could not find part " + partName + " in upload request", partNotFound);
//		}
//
//		return part.getInputStream();
//
//	}

}
