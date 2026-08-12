/*
=====================================================================
Name:    Christopher Crayton
Date:    August 11, 2026
Purpose: Front-end behavior for the Rolodex Contact Manager's View
         layer: the collapsible sidebar, the type-aware add-contact
         form, search/filter controls, and the scroll-driven flip
         card stack.

         IMPORTANT: `contacts` below is intentionally an empty array.
         Once the Controller layer (a Java HttpServer-based class) is
         built in a later week, the TODO comments in this file mark
         exactly where a fetch() call should replace this hardcoded
         empty array with real data from the backend, and where
         form submission / delete / edit actions should be wired to
         real API calls instead of doing nothing.
=====================================================================
*/

// ---------------------------------------------------------------
// Sample data placeholder - TODO (backend week): replace this with
// `let contacts = [];` populated by a fetch() to something like
// GET /api/contacts once the Controller and Model layers are wired
// together, instead of a value hardcoded here in the View.
// ---------------------------------------------------------------
var contacts = [];

var typeToClass = {
  "Business": "type-business",
  "Family": "type-family",
  "Friend": "type-friend",
  "Social Media": "type-social",
  "Personal": "type-personal"
};

var fieldLabels = {
  "Business": ["Company", "Title"],
  "Family": ["Relationship", "Birthday"],
  "Friend": ["How we met", "Favorite activity"],
  "Social Media": ["Platform", "Username"]
};

var filtered = [];
var pos = 0;
var activeType = "all";

var stackInner = document.getElementById("stackInner");
var emptyState = document.getElementById("emptyState");
var cardEls = [];

/* =====================================================================
   Sidebar: contact-type dropdown relabels the two "unique" fields
   ===================================================================== */
document.getElementById("contactType").addEventListener("change", function (e) {
  var labels = fieldLabels[e.target.value];
  var group = document.getElementById("uniqueFieldsGroup");
  if (!labels) {
    group.classList.add("hidden");
    return;
  }
  group.classList.remove("hidden");
  document.getElementById("uniqueField1Label").firstChild.textContent = labels[0];
  document.getElementById("uniqueField2Label").firstChild.textContent = labels[1];
});

/* =====================================================================
   Sidebar: collapse / expand toggle (pure front-end UI state)
   ===================================================================== */
document.getElementById("collapseBtn").addEventListener("click", function () {
  document.getElementById("mainGrid").classList.add("collapsed");
  document.querySelector(".sidebar-form").classList.add("hidden");
  document.getElementById("sidebarTitle").classList.add("hidden");
  document.getElementById("collapseBtn").classList.add("hidden");
  document.getElementById("expandBtn").classList.remove("hidden");
});
document.getElementById("expandBtn").addEventListener("click", function () {
  document.getElementById("mainGrid").classList.remove("collapsed");
  document.querySelector(".sidebar-form").classList.remove("hidden");
  document.getElementById("sidebarTitle").classList.remove("hidden");
  document.getElementById("collapseBtn").classList.remove("hidden");
  document.getElementById("expandBtn").classList.add("hidden");
});

/* =====================================================================
   Add-contact form submission
   TODO (backend week): replace this with a fetch("/api/contacts",
   { method: "POST", ... }) call to the Controller, using the values
   read below as the request body, then refresh `contacts` from the
   response instead of just clearing the form.
   ===================================================================== */
document.getElementById("addContactForm").addEventListener("submit", function (e) {
  e.preventDefault();
  var message = document.getElementById("addContactMessage");
  message.textContent = "Backend not connected yet - nothing was saved.";
  message.style.color = "#8a93a1";
  // TODO (backend week): send form values to the Controller here.
});

/* =====================================================================
   Quit button - no server to shut down yet, so this just confirms
   and, once the Controller exists, should POST to something like
   /api/quit before navigating to thankyou.html.
   ===================================================================== */
document.getElementById("quitButton").addEventListener("click", function () {
  var confirmed = confirm("Are you sure you want to quit the Rolodex application?");
  if (!confirmed) return;
  // TODO (backend week): fetch("/api/quit", { method: "POST" }) before navigating.
  window.location.href = "thankyou.html";
});

/* =====================================================================
   Card stack engine - builds one persistent DOM element per contact
   and repositions/re-labels them on every render() call. Safe to run
   with an empty `contacts` array; it simply shows the empty state.
   ===================================================================== */
function frontHtml(c) {
  var extra = c.f1
    ? '<p>' + c.f1 + ': ' + c.v1 + ' &middot; ' + c.f2 + ': ' + c.v2 + '</p>'
    : '';
  return (
    '<div class="card-name"><span>' + c.last + ', ' + c.first + '</span>' +
    '<span class="card-type-badge">' + c.type + '</span></div>' +
    '<p>' + c.phone + ' &middot; ' + c.email + '</p>' +
    '<p>' + c.address + '</p>' +
    extra +
    '<div class="card-buttons"><button class="edit-btn">Edit field</button><button class="delete-btn">Delete</button></div>'
  );
}

function backHtml(c, corner) {
  return (
    '<div class="card-back corner-' + corner + '">' +
    '<span class="card-letter">' + c.last.charAt(0).toUpperCase() + '</span>' +
    '</div>'
  );
}

/** (Re)builds the persistent card DOM elements to match the current `contacts` array. */
function buildCardElements() {
  stackInner.querySelectorAll(".contact-card").forEach(function (el) { el.remove(); });
  cardEls = [];
  contacts.forEach(function (c) {
    var el = document.createElement("div");
    el.className = "contact-card " + (typeToClass[c.type] || "type-personal");
    stackInner.appendChild(el);
    cardEls.push(el);
  });
}

function recomputeFiltered() {
  var query = document.getElementById("searchBox").value.trim().toLowerCase();
  filtered = [];
  contacts.forEach(function (c, i) {
    var full = (c.first + " " + c.last).toLowerCase();
    var matchesText = query === "" || full.indexOf(query) !== -1;
    var matchesType = activeType === "all" || c.type === activeType;
    if (matchesText && matchesType) filtered.push(i);
  });
  if (pos >= filtered.length) pos = 0;
  render();
}

function wrapIndex(i, length) {
  return ((i % length) + length) % length;
}

function render() {
  cardEls.forEach(function (el) { el.style.display = "none"; });

  if (filtered.length === 0) {
    emptyState.classList.remove("hidden");
    document.getElementById("letterBadge").textContent = "-";
    document.getElementById("posCounter").textContent = "0 of 0";
    rebuildLetterStrip();
    return;
  }
  emptyState.classList.add("hidden");

  var range = Math.min(3, Math.floor((filtered.length - 1) / 2) + 1);
  for (var offset = -range; offset <= range; offset++) {
    var idx = wrapIndex(pos + offset, filtered.length);
    var c = contacts[filtered[idx]];
    var el = cardEls[filtered[idx]];
    el.style.display = "block";
    el.style.height = offset === 0 ? "auto" : "132px";

    var ty, rot, opacity, scale, z;
    if (offset === 0) {
      ty = 0; rot = 0; opacity = 1; scale = 1; z = 100;
      el.innerHTML = frontHtml(c);
      wireCardButtons(el, c);
    } else {
      var mag = Math.abs(offset);
      var dir = offset < 0 ? -1 : 1;
      ty = dir * (mag * 46 + 26);
      rot = dir * mag * -15;
      opacity = Math.max(0.3, 1 - mag * 0.15);
      scale = 1 - mag * 0.035;
      z = 90 - mag;
      el.innerHTML = backHtml(c, offset < 0 ? "top-left" : "bottom-right");
    }
    el.style.zIndex = z;
    el.style.opacity = opacity;
    el.style.transform = "translateY(calc(-50% + " + ty + "px)) rotateX(" + rot + "deg) scale(" + scale + ")";
  }

  var current = contacts[filtered[pos]];
  document.getElementById("letterBadge").textContent = current.last.charAt(0).toUpperCase();
  document.getElementById("posCounter").textContent = (pos + 1) + " of " + filtered.length;
  rebuildLetterStrip();
}

/**
 * TODO (backend week): wire these to real fetch() calls -
 * PUT/POST /api/contacts/{id} for edit, DELETE /api/contacts/{id}
 * for delete - instead of doing nothing.
 */
function wireCardButtons(el, contact) {
  var editBtn = el.querySelector(".edit-btn");
  var deleteBtn = el.querySelector(".delete-btn");
  if (editBtn) editBtn.addEventListener("click", function () {
    // TODO (backend week): open the edit-a-field flow for `contact`.
  });
  if (deleteBtn) deleteBtn.addEventListener("click", function () {
    // TODO (backend week): confirm, then DELETE `contact` via the API.
  });
}

function rebuildLetterStrip() {
  var strip = document.getElementById("letterStrip");
  strip.innerHTML = "";
  var letters = [];
  filtered.forEach(function (i) {
    var L = contacts[i].last.charAt(0).toUpperCase();
    if (letters.indexOf(L) === -1) letters.push(L);
  });
  letters.forEach(function (letter) {
    var tab = document.createElement("button");
    tab.textContent = letter;
    tab.setAttribute("aria-label", "Jump to " + letter);
    tab.addEventListener("click", function () {
      var target = filtered.findIndex(function (i) {
        return contacts[i].last.charAt(0).toUpperCase() === letter;
      });
      if (target !== -1) { pos = target; render(); }
    });
    strip.appendChild(tab);
  });
}

document.getElementById("stackViewport").addEventListener("wheel", function (e) {
  e.preventDefault();
  if (filtered.length === 0) return;
  pos = wrapIndex(pos + (e.deltaY > 0 ? 1 : -1), filtered.length);
  render();
}, { passive: false });

document.getElementById("searchBox").addEventListener("input", recomputeFiltered);

document.querySelectorAll(".filter-btn").forEach(function (btn) {
  btn.addEventListener("click", function () {
    activeType = btn.getAttribute("data-type");
    document.querySelectorAll(".filter-btn").forEach(function (b) { b.classList.remove("active"); });
    btn.classList.add("active");
    pos = 0;
    recomputeFiltered();
  });
});

// ---------------------------------------------------------------
// Initial render. TODO (backend week): move this after the fetch()
// call that populates `contacts` from the Controller, so the stack
// renders real data instead of the empty state on page load.
// ---------------------------------------------------------------
buildCardElements();
recomputeFiltered();
