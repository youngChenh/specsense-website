interface CategoryLink {
  key: string
  nameEn?: string
  nameZh?: string
  children?: CategoryLink[]
}

// Keep valid keys intact; only resolve unambiguous category-name links.
export function resolveCategoryKey(value: string, categories: CategoryLink[]): string {
  if (value === 'all') return value

  const flat: CategoryLink[] = []
  function visit(nodes: CategoryLink[]) {
    for (const node of nodes) {
      flat.push(node)
      visit(node.children || [])
    }
  }
  visit(categories)

  if (flat.some(category => category.key === value)) return value
  const matches = flat.filter(category => category.nameZh === value || category.nameEn === value)
  return matches.length === 1 && matches[0]?.key ? matches[0].key : value
}
