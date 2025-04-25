/*
 * Copyright 2018-2025 The Code Department.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tcdng.unify.web.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.util.html.HtmlUtils;
import com.tcdng.unify.web.ui.WebUIApplicationComponents;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.ResponseWriterPool;

/**
 * HTML utilities tests.
 * 
 * @author Lateef Ojulari
 * @since 4.1
 */
public class HtmlUtilsTest extends AbstractUnifyComponentTest {

	private ResponseWriterPool pool;

	@Test
	public void parseTextToHTMLNull() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, null);
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLBlank() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "");
			HtmlUtils.parseTextToHTML(writer, "    ");
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLPlain() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "Hello World!");

			assertEquals("<p class=\"init\">Hello World!</p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLPlainBreakPre() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![break]Hello World!");

			assertEquals("<p class=\"init\"></p><p>Hello World!</p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLPlainBreakMid() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "Hello![break] World!");

			assertEquals("<p class=\"init\">Hello</p><p> World!</p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLPlainBreakPost() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "Hello World!![break]");

			assertEquals("<p class=\"init\">Hello World!</p><p></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLBold() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "Hello +[World!]");

			assertEquals("<p class=\"init\">Hello <strong>World!</strong></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLBoldEdge() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "+[Hello World!]");

			assertEquals("<p class=\"init\"><strong>Hello World!</strong></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLBoldEmpty() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "+[]Hello World!");

			assertEquals("<p class=\"init\"><strong></strong>Hello World!</p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLBoldEscape() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "Hello +[World!\\+]");

			assertEquals("<p class=\"init\">Hello <strong>World!+</strong></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLItalic() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "Hello ~[World!]");

			assertEquals("<p class=\"init\">Hello <em>World!</em></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLItalicEdge() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "~[Hello World!]");

			assertEquals("<p class=\"init\"><em>Hello World!</em></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLItalicEmpty() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "Hello ~[]World!");

			assertEquals("<p class=\"init\">Hello <em></em>World!</p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLItalicEscape() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "Hello ~[World!\\+]");

			assertEquals("<p class=\"init\">Hello <em>World!+</em></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLBoldItalic() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "He%[llo Wor]ld!");

			assertEquals("<p class=\"init\">He<strong><em>llo Wor</em></strong>ld!</p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLBoldItalicEdge() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "%[Hello World!]");

			assertEquals("<p class=\"init\"><strong><em>Hello World!</em></strong></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLBoldItalicEmpty() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "Hello World!%[]");

			assertEquals("<p class=\"init\">Hello World!<strong><em></em></strong></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLBoldItalicEscape() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "He%[llo\\% Wor]ld!");

			assertEquals("<p class=\"init\">He<strong><em>llo% Wor</em></strong>ld!</p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLList() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "*[Hello World!]");

			assertEquals("<p class=\"init\"><ul style=\"list-style-type:disc;margin-left:1em;\"><li>Hello World!</li></ul></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLListTypes() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "*[decimal Hello World!] *[Blue skies] *[disc How it works]");

			assertEquals("<p class=\"init\"><ul style=\"list-style-type:decimal;margin-left:1em;\"><li>Hello World!</li><li>Blue skies</li></ul><ul style=\"list-style-type:disc;margin-left:1em;\"><li>How it works</li></ul></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLListPostText() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "The day before tomorrow *[Hello World!]");

			assertEquals("<p class=\"init\">The day before tomorrow <ul style=\"list-style-type:disc;margin-left:1em;\"><li>Hello World!</li></ul></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLListPostTextBold() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "The day +[before] tomorrow *[Hello World!]");

			assertEquals("<p class=\"init\">The day <strong>before</strong> tomorrow <ul style=\"list-style-type:disc;margin-left:1em;\"><li>Hello World!</li></ul></p>",
					writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLListPreText() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "*[Hello World!] I'm getting it done.");

			assertEquals("<p class=\"init\"><ul style=\"list-style-type:disc;margin-left:1em;\"><li>Hello World!</li></ul>I'm getting it done.</p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLListPreTextEscapeless() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "*[Hello *World!] I'm ~getting $$$it done.");

			assertEquals("<p class=\"init\"><ul style=\"list-style-type:disc;margin-left:1em;\"><li>Hello *World!</li></ul>I'm ~getting $$$it done.</p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLListPreTextItalic() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "*[Hello World!] I'm ~[getting it] done.");

			assertEquals("<p class=\"init\"><ul style=\"list-style-type:disc;margin-left:1em;\"><li>Hello World!</li></ul>I'm <em>getting it</em> done.</p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLListMultiple() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "*[Hello World!]  \n*[Blue Skies!]");

			assertEquals("<p class=\"init\"><ul style=\"list-style-type:disc;margin-left:1em;\"><li>Hello World!</li><li>Blue Skies!</li></ul></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLListMultipleWithInterruption() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "*[Hello World!] Melody  \n*[Blue Skies!]");

			assertEquals("<p class=\"init\"><ul style=\"list-style-type:disc;margin-left:1em;\"><li>Hello World!</li></ul>Melody  \n<ul style=\"list-style-type:disc;margin-left:1em;\"><li>Blue Skies!</li></ul></p>",
					writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLListBold() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "*[Hello +[World!]]");

			assertEquals("<p class=\"init\"><ul style=\"list-style-type:disc;margin-left:1em;\"><li>Hello <strong>World!</strong></li></ul></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLListMultipleItalicBold() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "*[~[Hello] World!]  \n*[+[Blue] Skies!]");

			assertEquals("<p class=\"init\"><ul style=\"list-style-type:disc;margin-left:1em;\"><li><em>Hello</em> World!</li><li><strong>Blue</strong> Skies!</li></ul></p>",
					writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLListMultipleWithInterruptionItalicBold() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "*[Hello %[World]!] ~[Melody]  \n*[Blue Skies!]");

			assertEquals(
					"<p class=\"init\"><ul style=\"list-style-type:disc;margin-left:1em;\"><li>Hello <strong><em>World</em></strong>!</li></ul><em>Melody</em>  \n<ul style=\"list-style-type:disc;margin-left:1em;\"><li>Blue Skies!</li></ul></p>",
					writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseTextToHTMLListMultipleItalicBoldBreak() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "*[~[Hello] World!]  \n*![break][+[Blue] Skies!]");

			assertEquals("<p class=\"init\"><ul style=\"list-style-type:disc;margin-left:1em;\"><li><em>Hello</em> World!</li><li><strong>Blue</strong> Skies!</li></ul></p>",
					writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLListMultipleItalicBoldBreakPass() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "*[~[Hello] World!]  \n*[+[Blue] Skies!]![break]House on the Rock.");

			assertEquals(
					"<p class=\"init\"><ul style=\"list-style-type:disc;margin-left:1em;\"><li><em>Hello</em> World!</li><li><strong>Blue</strong> Skies!</li></ul></p><p>House on the Rock.</p>",
					writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLLink() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![<www.flowcentralplatform.com>]");

			assertEquals("<p class=\"init\"><a href=\"www.flowcentralplatform.com\"></a></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}

		writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![link<www.flowcentralplatform.com>]");

			assertEquals("<p class=\"init\"><a href=\"www.flowcentralplatform.com\"></a></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLLinkText() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![<www.flowcentralplatform.com>FlowCentral]");

			assertEquals("<p class=\"init\"><a href=\"www.flowcentralplatform.com\">FlowCentral</a></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}

		writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![link<www.flowcentralplatform.com>FlowCentral]");

			assertEquals("<p class=\"init\"><a href=\"www.flowcentralplatform.com\">FlowCentral</a></p>", writer.toString());
		} finally

		{
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLTLink() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![tlink<www.flowcentralplatform.com>]");

			assertEquals("<p class=\"init\"><a href=\"www.flowcentralplatform.com\" target=\"_blank\"></a></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLTLinkText() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![tlink<www.flowcentralplatform.com>Technologies]");

			assertEquals("<p class=\"init\"><a href=\"www.flowcentralplatform.com\" target=\"_blank\">Technologies</a></p>",
					writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLSLink() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![slink<sectionA>]");

			assertEquals("<p class=\"init\"><a href=\"#sectionA\"></a></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLSLinkText() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![slink<sectionA>Blue Albatross]");

			assertEquals("<p class=\"init\"><a href=\"#sectionA\">Blue Albatross</a></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLEmail() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![email<lateef.ojulari@tcdng.com>]");

			assertEquals("<p class=\"init\"><a href=\"mailto:lateef.ojulari@tcdng.com\"></a></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLEmailText() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![email<lateef.ojulari@tcdng.com>Send Email]");

			assertEquals("<p class=\"init\"><a href=\"mailto:lateef.ojulari@tcdng.com\">Send Email</a></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLTel() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![tel<+234 8020948192>]");

			assertEquals("<p class=\"init\"><a href=\"tel:+234 8020948192\"></a></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLTelText() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![tel<+234 8020948192>Call Us]");
			assertEquals("<p class=\"init\"><a href=\"tel:+234 8020948192\">Call Us</a></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLDown() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![down<HowStuffWorks.pdf>]");
			assertEquals("<p class=\"init\"><a href=\"HowStuffWorks.pdf\" download></a></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLDownText() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer, "![down<HowStuffWorks.pdf>Get Latest WhitePaper!]");
			assertEquals("<p class=\"init\"><a href=\"HowStuffWorks.pdf\" download>Get Latest WhitePaper!</a></p>", writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLListPreTextLink() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer,
					"*[Hello World!] I'm getting ![<www.flowcentralplatform.com>FlowCentral] it done.");
			assertEquals(
					"<p class=\"init\"><ul style=\"list-style-type:disc;margin-left:1em;\"><li>Hello World!</li></ul>I'm getting <a href=\"www.flowcentralplatform.com\">FlowCentral</a> it done.</p>",
					writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Test
	public void parseTextToHTMLListMultipleItalicBoldTLink() throws Exception {
		ResponseWriter writer = pool.getResponseWriter();
		try {
			HtmlUtils.parseTextToHTML(writer,
					"*[~[Hello] World!]  \n*[+[Blue] ![tlink<www.flowcentralplatform.com>Technologies] Skies!]");
			assertEquals(
					"<p class=\"init\"><ul style=\"list-style-type:disc;margin-left:1em;\"><li><em>Hello</em> World!</li><li><strong>Blue</strong> <a href=\"www.flowcentralplatform.com\" target=\"_blank\">Technologies</a> Skies!</li></ul></p>",
					writer.toString());
		} finally {
			pool.restore(writer);
		}
	}

	@Override
	protected void onSetup() throws Exception {
		pool = (ResponseWriterPool) getComponent(WebUIApplicationComponents.APPLICATION_RESPONSEWRITERPOOL);
	}

	@Override
	protected void onTearDown() throws Exception {

	}

}
