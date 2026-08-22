/*
=====================================================================
Name:    Christopher Crayton
Date:    August 11, 2026
Purpose: Front-end behavior for the Rolodex Contact Manager's View
         layer: the collapsible sidebar, the type-aware add-contact
         form, search/filter controls, and the scroll-driven flip
         card stack.

Part 2
Name:    Christopher Crayton
Date:    August 20, 2026
Purpose: Backend connected. Every TODO from last week is now a real
         fetch() call to the Controller layer (see src/controller/):
         loading contacts, adding a contact, editing a single field
         (via the new edit modal), deleting a contact, and quitting
         the application. `contacts` is now populated from the server
         instead of being hardcoded here in the View.

         Also fixed a rendering bug found while testing with real,
         filtered data: with only 1-2 search results, wrapIndex()
         could point two different card offsets at the same physical
         DOM element, and whichever one rendered last (often a "back"
         state) would silently overwrite the correct front-card
         display. render() now tracks which physical elements have
         already been drawn each frame and skips duplicates.

Part 3
Name:    Christopher Crayton
Date:    August 21, 2026
Purpose: Added Friend and Social Media to typeToClass and fieldLabels,
         matching the new FriendContact/SocialMediaContact model
         classes and their backend support.

Part 4
Name:    Christopher Crayton
Date:    August 22, 2026
Purpose: loadContacts() now sorts the fetched contacts alphabetically
         by last name (then first name) before rendering, instead of
         leaving them grouped by contact-type table the way the
         backend returns them. rebuildLetterStrip() also sorts its
         letters A-Z as a second safeguard. Together these fix the
         letter tabs (and the flip-through order itself) appearing in
         an arbitrary, non-alphabetical order.
=====================================================================
*/

var contacts = []; // populated by loadContacts() below, from GET /api/contacts

var typeToClass = {
  "Business": "type-business",
  "Family": "type-family",
  "Personal": "type-personal",
  "Friend": "type-friend",
  "Social Media": "type-social"
};

var fieldLabels = {
  "Business": ["Company", "Title"],
  "Family": ["Relationship", "Birthday"],
  "Personal": ["Notes", null],
  "Friend": ["How we met", "Favorite activity"],
  "Social Media": ["Platform", "Username"]
};

var filtered = [];
var pos = 0;
var activeType = "all";
var contactBeingEdited = null; // tracks which contact the edit modal is currently open for

var stackInner = document.getElementById("stackInner");
var emptyState = document.getElementById("emptyState");
var cardEls = [];

/* =====================================================================
   Loading contacts from the backend
   ===================================================================== */
function loadContacts() {
  fetch("/api/contacts")
    .then(function (response) { return response.json(); })
    .then(function (data) {
      // The backend returns contacts grouped by table (Business, then
      // Family, then Personal, etc.), so a global sort by last name
      // (then first name) is needed here for the flip stack and the
      // letter tabs to move through the alphabet in true order rather
      // than jumping between contact-type groups.
      data.sort(function (a, b) {
        var lastCompare = a.last.toLowerCase().localeCompare(b.last.toLowerCase());
        if (lastCompare !== 0) return lastCompare;
        return a.first.toLowerCase().localeCompare(b.first.toLowerCase());
      });
      contacts = data;
      buildCardElements();
      pos = 0;
      recomputeFiltered();
    })
    .catch(function (error) { console.error("Failed to load contacts:", error); });
}

/* =====================================================================
   Sidebar: contact-type dropdown relabels the two "unique" fields
   ===================================================================== */
document.getElementById("contactType").addEventListener("change", function (e) {
  var labels = fieldLabels[e.target.value];
  var group = document.getElementById("uniqueFieldsGroup");
  var field2Label = document.getElementById("uniqueField2Label");
  if (!labels) {
    group.classList.add("hidden");
    return;
  }
  group.classList.remove("hidden");
  document.getElementById("uniqueField1Label").firstChild.textContent = labels[0];
  if (labels[1]) {
    field2Label.classList.remove("hidden");
    field2Label.firstChild.textContent = labels[1];
  } else {
    field2Label.classList.add("hidden"); // e.g. Personal only has one unique field (Notes)
  }
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
   Add-contact form submission -> POST /api/contacts
   ===================================================================== */
document.getElementById("addContactForm").addEventListener("submit", function (e) {
  e.preventDefault();
  var message = document.getElementById("addContactMessage");

  var params = new URLSearchParams();
  params.set("contactType", document.getElementById("contactType").value);
  params.set("firstName", document.getElementById("firstName").value);
  params.set("lastName", document.getElementById("lastName").value);
  params.set("phoneNumber", document.getElementById("phoneNumber").value);
  params.set("email", document.getElementById("email").value);
  params.set("street", document.getElementById("street").value);
  params.set("city", document.getElementById("city").value);
  params.set("state", document.getElementById("state").value);
  params.set("zipCode", document.getElementById("zipCode").value);
  params.set("uniqueField1", document.getElementById("uniqueField1").value);
  params.set("uniqueField2", document.getElementById("uniqueField2").value);

  fetch("/api/contacts", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: params.toString()
  })
    .then(function (response) {
      return response.json().then(function (data) { return { ok: response.ok, data: data }; });
    })
    .then(function (result) {
      if (!result.ok) throw new Error(result.data.error || "Failed to add contact.");
      message.textContent = "Contact added.";
      message.style.color = "#1e7e42";
      document.getElementById("addContactForm").reset();
      document.getElementById("uniqueFieldsGroup").classList.add("hidden");
      loadContacts();
    })
    .catch(function (error) {
      message.textContent = error.message;
      message.style.color = "#a33333";
    });
});

/* =====================================================================
   Quit button -> POST /api/quit, then navigate to thankyou.html
   ===================================================================== */
document.getElementById("quitButton").addEventListener("click", function () {
  var confirmed = confirm("Are you sure you want to quit the Rolodex application?");
  if (!confirmed) return;

  fetch("/api/quit", { method: "POST" })
    .then(function () { window.location.href = "thankyou.html"; })
    .catch(function () { window.location.href = "thankyou.html"; }); // server is shutting down either way
});

/* =====================================================================
   Card stack engine - builds one persistent DOM element per contact
   and repositions/re-labels them on every render() call.
   ===================================================================== */
function frontHtml(c) {
  var extra = c.f1
    ? '<p>' + c.f1Label + ': ' + c.f1 + (c.f2Key ? ' &middot; ' + c.f2Label + ': ' + c.f2 : '') + '</p>'
    : '';
  return (
    '<div class="card-name"><span>' + c.last + ', ' + c.first + '</span>' +
    '<span class="card-type-badge">' + c.type + '</span></div>' +
    '<p>' + c.phone + ' &middot; ' + (c.email || 'N/A') + '</p>' +
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

  // Render offsets outward from the center (0, 1, -1, 2, -2, ...) and
  // skip any offset whose wrapped index has already been rendered this
  // frame. This matters when there are only 1 or 2 filtered results:
  // without it, wrapIndex() can point two different offsets at the same
  // physical card element, and whichever offset is processed last
  // (usually landing on a "back" state) would silently overwrite the
  // correct front-card render for that element.
  var range = Math.min(3, Math.floor((filtered.length - 1) / 2) + 1);
  var offsetOrder = [0];
  for (var d = 1; d <= range; d++) { offsetOrder.push(d, -d); }
  var renderedIndices = {};

  offsetOrder.forEach(function (offset) {
    var idx = wrapIndex(pos + offset, filtered.length);
    if (renderedIndices[idx]) return; // this physical card already rendered this frame
    renderedIndices[idx] = true;

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
  });

  var current = contacts[filtered[pos]];
  document.getElementById("letterBadge").textContent = current.last.charAt(0).toUpperCase();
  document.getElementById("posCounter").textContent = (pos + 1) + " of " + filtered.length;
  rebuildLetterStrip();
}

/** Wires the front card's Edit field / Delete buttons to the real backend. */
function wireCardButtons(el, contact) {
  var editBtn = el.querySelector(".edit-btn");
  var deleteBtn = el.querySelector(".delete-btn");
  if (editBtn) editBtn.addEventListener("click", function () { openEditModal(contact); });
  if (deleteBtn) deleteBtn.addEventListener("click", function () { deleteContact(contact); });
}

/* =====================================================================
   Delete -> POST /api/contacts/delete
   ===================================================================== */
function deleteContact(contact) {
  var confirmed = confirm("Delete " + contact.first + " " + contact.last + "? This cannot be undone.");
  if (!confirmed) return;

  var params = new URLSearchParams();
  params.set("id", contact.id);
  params.set("contactType", contact.type);

  fetch("/api/contacts/delete", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: params.toString()
  })
    .then(function () { loadContacts(); })
    .catch(function (error) { console.error("Delete failed:", error); });
}

/* =====================================================================
   Edit-a-field modal
   ===================================================================== */
function openEditModal(contact) {
  contactBeingEdited = contact;

  document.getElementById("editModalContactName").textContent =
    contact.first + " " + contact.last + " (" + contact.type + ")";

  var fieldSelect = document.getElementById("editFieldSelect");
  var options = [
    ["first_name", "First Name"],
    ["last_name", "Last Name"],
    ["phone_number", "Phone Number"],
    ["email", "Email"],
    ["street", "Street"],
    ["city", "City"],
    ["state", "State"],
    ["zip_code", "Zip Code"]
  ];
  if (contact.f1Key) options.push([contact.f1Key, contact.f1Label]);
  if (contact.f2Key) options.push([contact.f2Key, contact.f2Label]);

  fieldSelect.innerHTML = options
    .map(function (o) { return '<option value="' + o[0] + '">' + o[1] + '</option>'; })
    .join("");

  document.getElementById("editFieldValue").value = "";
  document.getElementById("editMessage").textContent = "";
  document.getElementById("editModal").classList.remove("hidden");
}

function closeEditModal() {
  document.getElementById("editModal").classList.add("hidden");
  contactBeingEdited = null;
}

document.getElementById("cancelEditButton").addEventListener("click", closeEditModal);

document.getElementById("saveEditButton").addEventListener("click", function () {
  var fieldKey = document.getElementById("editFieldSelect").value;
  var newValue = document.getElementById("editFieldValue").value;
  var message = document.getElementById("editMessage");

  var params = new URLSearchParams();
  params.set("id", contactBeingEdited.id);
  params.set("contactType", contactBeingEdited.type);
  params.set("fieldKey", fieldKey);
  params.set("newValue", newValue);

  fetch("/api/contacts/update", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: params.toString()
  })
    .then(function (response) {
      return response.json().then(function (data) { return { ok: response.ok, data: data }; });
    })
    .then(function (result) {
      if (!result.ok) throw new Error(result.data.error || "Failed to update contact.");
      closeEditModal();
      loadContacts();
    })
    .catch(function (error) {
      message.textContent = error.message;
      message.style.color = "#a33333";
    });
});

function rebuildLetterStrip() {
  var strip = document.getElementById("letterStrip");
  strip.innerHTML = "";
  var letters = [];
  filtered.forEach(function (i) {
    var L = contacts[i].last.charAt(0).toUpperCase();
    if (letters.indexOf(L) === -1) letters.push(L);
  });
  letters.sort(); // A-Z, independent of the order contacts happen to appear in `filtered`
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
// Initial load - fetches real contacts from the Controller layer.
// ---------------------------------------------------------------
loadContacts();
